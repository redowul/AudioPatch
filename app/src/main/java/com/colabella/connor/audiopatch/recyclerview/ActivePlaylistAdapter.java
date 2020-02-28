package com.colabella.connor.audiopatch.recyclerview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.controllers.SingletonController;
import com.colabella.connor.audiopatch.controllers.BottomSheetController;
import com.colabella.connor.audiopatch.controllers.ActivePlaylistController;
import com.colabella.connor.audiopatch.Equalizer;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.nearbyconnections.PayloadController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivePlaylistAdapter extends RecyclerView.Adapter<ActivePlaylistAdapter.ViewHolder> implements SwipeAndDragHelper.ActionCompletionContract {
    private static List<Audio> dataSet = new ArrayList<>();
    private ItemTouchHelper itemTouchHelper;

    private Audio getSelectedItem(int index) {
        return dataSet.get(index);
    }

    public void addItem(Audio item) {
        dataSet.add(item);
        if (dataSet.size() == 1) {
            BottomSheetController bottomSheetController = new BottomSheetController();
            bottomSheetController.alterBottomSheet(item);
            setSelectedAudio(0);

            ActivePlaylistController activePlaylistController = new ActivePlaylistController();
            if (!SingletonController.getInstance().isGuest()) {
                activePlaylistController.initializeMediaPlayer(getSelectedAudio());
            }
        }
    }

    public void removeAllItems() {
        for (int i = dataSet.size() - 1; i > 0; i--) {
            dataSet.remove(i);
        }
    }

    public Audio getSelectedAudio() {
        return getSelectedItem(getCurrentlySelectedItemIndex());
    }

    protected void setSelectedAudio(int selectedAudioPos) {
        for (int i = 0; i < getItemCount(); i++) {
            dataSet.get(i).setSelected(false);
            if (i == selectedAudioPos) {
                dataSet.get(i).setSelected(true);
            }
            notifyItemChanged(i);
        }
        SingletonController.getInstance().getActivePlaylistAdapter().notifyDataSetChanged();
    }

    protected int getCurrentlySelectedItemIndex() {
        if (dataSet != null) {
            if (dataSet.size() > 0) {
                for (int i = 0; i < dataSet.size(); i++) {
                    if (dataSet.get(i).isSelected()) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public boolean isItemSelected() {
        if (dataSet != null) {
            if (dataSet.size() > 0) {
                for (int i = 0; i < dataSet.size(); i++) {
                    if (dataSet.get(i).isSelected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected Audio getAudioAtIndex(int index) {
        return dataSet.get(index);
    }

    @Override
    public ActivePlaylistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_playlist_item, parent, false);
        return new ActivePlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ActivePlaylistAdapter.ViewHolder holder, int position) {
        if (dataSet.size() > 0) {
            Audio audio = dataSet.get(position);
            String title = audio.getTitle();
            holder.itemTitle.setText(title);

            String artist = audio.getArtist();
            holder.itemArtist.setText(artist);

            String duration = audio.getDuration();
            holder.itemDuration.setText(duration);

            MainActivity mainActivity = new MainActivity();
            String submitter = mainActivity.getInstance().getResources().getString(R.string.submitter, audio.getSubmitter());

            holder.itemSubmitter.setText(submitter);

            Bitmap albumArt = dataSet.get(position).getAlbumArt();
            if (albumArt != null) {
                holder.albumArt.setImageBitmap(albumArt);
            } else {
                holder.albumArt.setImageResource(R.drawable.audiopatch_logo_square);
            }

            holder.itemHandle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper.startDrag(holder);
                    }
                    return false;
                }
            });

            holder.itemView.setBackgroundResource(R.color.recyclerViewPrimary); // Sets all items to primary background color, representing none being selected.

            holder.equalizer = holder.itemView.findViewById(R.id.equalizer);

            if (dataSet.get(position).isSelected()) { // If isSelected returns true, highlight the item.
                holder.equalizer.setVisibility(View.VISIBLE);
                holder.itemView.setBackgroundResource(R.color.recyclerViewDark);
                ActivePlaylistController activePlaylistController = new ActivePlaylistController();
                if (activePlaylistController.getMediaPlayer() != null) {
                    if (activePlaylistController.getMediaPlayer().isPlaying()) {
                        holder.equalizer.animateBars();
                    } else {
                        if (holder.equalizer.isAnimating()) {
                            holder.equalizer.stopBars();
                        }
                    }
                } else {
                    if (holder.equalizer.isAnimating()) {
                        holder.equalizer.stopBars();
                    }
                }
            } else {
                holder.equalizer.setVisibility(View.GONE);
                holder.equalizer.stopBars();
            }
        } else {
            MainActivity mainActivity = new MainActivity();
            Bitmap blurredAlbumCover = BitmapFactory.decodeResource(mainActivity.getInstance().getResources(),
                    R.drawable.audiopatch_logo_square_blurrable); // getting the resource, it isn't blurred yet

            ImageView bottomSheetCapstoneAlbumCover = mainActivity.getInstance().findViewById(R.id.bottom_sheet_current_album_cover_small);
            bottomSheetCapstoneAlbumCover.setImageBitmap(null);
            bottomSheetCapstoneAlbumCover.setImageBitmap(blurredAlbumCover); // Set background of the bottom sheet capstone image; this one isn't blurred yet
            bottomSheetCapstoneAlbumCover.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        BottomSheetController bottomSheetController = new BottomSheetController();
        if (dataSet.size() == 0) {
            bottomSheetController.resetBottomSheet();
        }
        return dataSet.size();
    }

    @Override
    public void onViewMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(dataSet, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(dataSet, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        if (dataSet.get(position).isSelected()) {
            ActivePlaylistController activePlaylistController = new ActivePlaylistController();
            activePlaylistController.releaseSelectedAudio();

            BottomSheetController bottomSheetController = new BottomSheetController();
            bottomSheetController.resetBottomSheet();
        }
        dataSet.remove(position);
        notifyItemRemoved(position);

    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.itemTouchHelper = touchHelper;
    }

    /******** View Holder Class */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView itemTitle;
        private TextView itemArtist;
        private TextView itemDuration;
        private TextView itemSubmitter;
        private ImageView albumArt;
        private View itemHandle;
        private Equalizer equalizer;

        private ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.audio_title);
            itemArtist = itemView.findViewById(R.id.artist);
            itemDuration = itemView.findViewById(R.id.audio_duration);
            itemSubmitter = itemView.findViewById(R.id.submitter);
            albumArt = itemView.findViewById(R.id.album_art);
            itemHandle = itemView.findViewById(R.id.item_handle);
            itemHandle.setOnClickListener(this);
            equalizer = itemView.findViewById(R.id.equalizer);

            View itemPanel = itemView.findViewById(R.id.item_panel);
            itemPanel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ActivePlaylistController activePlaylistController = new ActivePlaylistController();
            if (getAdapterPosition() >= 0) {
                setSelectedAudio(getAdapterPosition());             // Set item at clicked position's isClicked to true
                activePlaylistController.initializeMediaPlayer(getAudioAtIndex(getCurrentlySelectedItemIndex()));
                activePlaylistController.startMediaPlayer();
                activePlaylistController.togglePlayButtonState();

                PayloadController payloadController = new PayloadController();
                payloadController.sendUpdate(); // distribute currently playing item data to all connected guests
            }
        }
    }
}