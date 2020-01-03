package com.colabella.connor.audiopatch.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioSingleton;
import com.colabella.connor.audiopatch.Controller;
import com.colabella.connor.audiopatch.Equalizer;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.qhutch.bottomsheetlayout.BottomSheetLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivePlaylistAdapter extends RecyclerView.Adapter<ActivePlaylistAdapter.ViewHolder> implements SwipeAndDragHelper.ActionCompletionContract {
    // private static ActivePlaylistAdapter instance;
    private static List<Audio> dataSet = new ArrayList<>();
    private ItemTouchHelper itemTouchHelper;

    //TODO only bother initializing after the user is confirmed for hosting?
   /* public static ActivePlaylistAdapter getInstance() {
        if (instance == null) {
            instance = new ActivePlaylistAdapter();
        }
        return instance;
    }*/

    Audio getSelectedItem(int index) {
        return dataSet.get(index);
    }

    void addItem(Audio item) {
        dataSet.add(item);
        if(dataSet.size() == 1) {
            ActivePlaylistController activePlaylistController = new ActivePlaylistController();
            activePlaylistController.alterBottomSheet(item);
            setSelectedAudio(0);
        }
    }

    void clearSelectedItem() {
        for (int i = 0; i < getItemCount(); i++) {
            dataSet.get(i).setSelected(false);
            notifyItemChanged(i);
        }
        AudioSingleton.getInstance().getActivePlaylistAdapter().notifyDataSetChanged();
    }

    public Audio getSelectedAudio() {
        return getSelectedItem(getCurrentlySelectedItemIndex());
    }

    void setSelectedAudio(int selectedAudioPos) {
        for (int i = 0; i < getItemCount(); i++) {
            dataSet.get(i).setSelected(false);
            if (i == selectedAudioPos) {
                dataSet.get(i).setSelected(true);
                System.out.println("Item at index " + i + " is selected");
            }
            notifyItemChanged(i);
        }
        AudioSingleton.getInstance().getActivePlaylistAdapter().notifyDataSetChanged();
    }

    int getCurrentlySelectedItemIndex() {
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

    Audio getAudioAtIndex(int index) {
        return dataSet.get(index);
    }

    @Override
    public ActivePlaylistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_layout, parent, false);
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
                holder.albumArt.setImageResource(R.drawable.audiopatchlogosquare);
            }

            holder.itemHandle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        Controller controller = new Controller();
                        if (controller.getUser().getRecyclerViewPermission()) {
                            itemTouchHelper.startDrag(holder);
                        }
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
                    }
                    else {
                        if(holder.equalizer.isAnimating()) {
                            holder.equalizer.stopBars();
                        }
                    }
                } else {
                    if(holder.equalizer.isAnimating()) {
                        holder.equalizer.stopBars();
                    }
                }
            } else {
                holder.equalizer.setVisibility(View.GONE);
                holder.equalizer.stopBars();
            }
            Controller controller = new Controller();
            if (controller.getUser().getRecyclerViewPermission()) {
                holder.itemView.findViewById(R.id.item_handle).setVisibility(View.VISIBLE);
            } else {
                holder.itemView.findViewById(R.id.item_handle).setVisibility(View.GONE);
            }
        }
        else {
            MainActivity mainActivity = new MainActivity();
            Bitmap blurredAlbumCover = BitmapFactory.decodeResource(mainActivity.getInstance().getResources(), R.drawable.audiopatchlogosquareblurrable); // getting the resource, it isn't blurred yet

            ImageView bottomSheetCapstoneAlbumCover = mainActivity.getInstance().findViewById(R.id.bottom_sheet_current_album_cover_small);
            bottomSheetCapstoneAlbumCover.setImageBitmap(null);
            bottomSheetCapstoneAlbumCover.setImageBitmap(blurredAlbumCover); // Set background of the bottom sheet capstone image; this one isn't blurred yet
            bottomSheetCapstoneAlbumCover.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        MainActivity mainActivity = new MainActivity();
        Button backButton = mainActivity.getInstance().findViewById(R.id.back_button);
        Button playButton = mainActivity.getInstance().findViewById(R.id.play_button);
        Button nextButton = mainActivity.getInstance().findViewById(R.id.next_button);
        Button expandButton = mainActivity.getInstance().findViewById(R.id.expand_bottom_sheet_button);

        // resets the background images within the bottom sheet when the active playlist size is reduced to 0
        if (dataSet.size() == 0) {
            BottomSheetLayout layout = mainActivity.getInstance().findViewById(R.id.bottom_sheet_layout);
            ImageView bottomSheetCapstoneAlbumCover = mainActivity.getInstance().findViewById(R.id.bottom_sheet_current_album_cover_small);
            bottomSheetCapstoneAlbumCover.setImageBitmap(null);
            if(layout.isExpended()) {
                layout.collapse();
            }
            else {
                Bitmap blurredAlbumCover = BitmapFactory.decodeResource(mainActivity.getInstance().getResources(), R.drawable.audiopatchlogosquareblurrable); // getting the resource, it isn't blurred yet

                ActivePlaylistController activePlaylistController = new ActivePlaylistController();
                blurredAlbumCover = activePlaylistController.blur(mainActivity.getInstance(), blurredAlbumCover); // blur the image
                ImageView bottomSheetAlbumCover = mainActivity.getInstance().findViewById(R.id.bottom_sheet_album_cover);
                bottomSheetAlbumCover.setImageBitmap(blurredAlbumCover);   // Set background of the bottom sheet
            }

            TextView bottomSheetCapstoneTitle = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_title);
            TextView bottomSheetCapstoneArtist = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_artist);

            TextView bottomSheetTitle = mainActivity.getInstance().findViewById(R.id.bottom_sheet_title);
            TextView bottomSheetArtist = mainActivity.getInstance().findViewById(R.id.bottom_sheet_artist);
            TextView bottomSheetSubmitter = mainActivity.getInstance().findViewById(R.id.bottom_sheet_submitter);

            bottomSheetCapstoneTitle.setText("");
            bottomSheetCapstoneArtist.setText("");
            bottomSheetTitle.setText("");
            bottomSheetArtist.setText("");
            bottomSheetSubmitter.setText("");

            backButton.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        }
        else {
            expandButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
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
        Controller controller = new Controller();
        if (controller.getUser().getRecyclerViewPermission()) {
            if (dataSet.get(position).isSelected()) {
                ActivePlaylistController activePlaylistController = new ActivePlaylistController();
                activePlaylistController.releaseSelectedAudio();
            }
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.itemTouchHelper = touchHelper;
    }

    /******** View Holder Class*/
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView itemTitle;
        private TextView itemArtist;
        //private TextView itemInterpunct;
        private TextView itemDuration;
        //private TextView itemSubmitterIntroText;
        private TextView itemSubmitter;
        private ImageView albumArt;
        private View itemHandle;
        private Equalizer equalizer;

        private ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.audio_title);
            //title.setOnClickListener(this);

            itemArtist = itemView.findViewById(R.id.artist);
            //itemInterpunct = itemView.findViewById(R.id.interpunct);
            itemDuration = itemView.findViewById(R.id.audio_duration);
            //itemSubmitterIntroText = itemView.findViewById(R.id.submitter_intro_text);
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
            Controller controller = new Controller();
            ActivePlaylistController activePlaylistController = new ActivePlaylistController();
            if (controller.getUser().getRecyclerViewPermission()) {
                if (getAdapterPosition() >= 0) {
                    setSelectedAudio(getAdapterPosition());             // Set item at clicked position's isClicked to true
                    activePlaylistController.playSelectedAudio(getAudioAtIndex(getCurrentlySelectedItemIndex()));
                    activePlaylistController.togglePlayButtonState();
                }
            }
        }
    }
}