package com.colabella.connor.audiopatch.RecyclerView;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Controller;
import com.colabella.connor.audiopatch.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivePlaylistAdapter extends RecyclerView.Adapter<ActivePlaylistAdapter.ViewHolder> implements SwipeAndDragHelper.ActionCompletionContract{
    private List<Audio> dataSet = new ArrayList<>();
    private ItemTouchHelper itemTouchHelper;

    ActivePlaylistAdapter() {
        //TODO only bother initializing after the user is confirmed for hosting?
        //dataSet = new ArrayList<>();
    }

    Audio getSelectedItem(int index) {
        return dataSet.get(index);
    }

    void addItem(Audio item){
        dataSet.add(item);
    }

    void setSelectedAudio(int selectedAudioPos){
        for(Audio item: dataSet) { item.setSelected(false); }
        dataSet.get(selectedAudioPos).setSelected(true);
        for (int i = 0; i < getItemCount(); i++) { notifyItemChanged(i); }
    }

    @Override
    public ActivePlaylistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_layout,parent,false);
        return new ActivePlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ActivePlaylistAdapter.ViewHolder holder, int position) {
        if(dataSet.size() > 0) {
            Audio audio = dataSet.get(position);
            String title = audio.getTitle();
            holder.itemTitle.setText(title);

            String artist = audio.getArtist();
            holder.itemArtist.setText(artist);

            String duration = audio.getDuration();
            holder.itemDuration.setText(duration);

            String submitter = audio.getSubmitter();
            //TODO change this back when usernames are implemented
            //holder.itemSubmitter.setText(submitter);

            Bitmap albumArt = dataSet.get(position).getAlbumArt();
            if (albumArt != null) {
                holder.albumArt.setImageBitmap(albumArt);
                holder.itemSubmitter.setText("Passed");
            }
            else {
                holder.albumArt.setImageResource(R.drawable.audiopatchlogosquare);
                holder.itemSubmitter.setText("Failed");
            }

        holder.itemHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    Controller c = new Controller();
                    if (c.getUser().getRecyclerViewPermission()) {
                        itemTouchHelper.startDrag(holder);
                    }
                }
                return false;
            }
        });

        holder.itemView.setBackgroundResource(R.color.recyclerViewPrimary); // Sets all items to primary background color, representing none being selected.
        if(dataSet.get(position).getSelected()) { // If isSelected returns true, highlight the item.
            holder.itemView.setBackgroundResource(R.color.recyclerViewAccent);
        }
        Controller controller = new Controller();
        if (controller.getUser().getRecyclerViewPermission()) {
            holder.itemView.findViewById(R.id.item_handle).setVisibility(View.VISIBLE);
        }
        else { holder.itemView.findViewById(R.id.item_handle).setVisibility(View.GONE); }
        }
    }

    @Override
    public int getItemCount() {
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
            if (dataSet.get(position).getSelected()) {
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
        private View itemPanel;

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

            itemPanel = itemView.findViewById(R.id.item_panel);
            itemPanel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Controller controller = new Controller();
            ActivePlaylistController activePlaylistController = new ActivePlaylistController();
            if (controller.getUser().getRecyclerViewPermission()) {
                setSelectedAudio(getAdapterPosition());             // Set item at clicked position's isClicked to true
                activePlaylistController.playSelectedItem(dataSet.get(getAdapterPosition()));
                activePlaylistController.togglePlayButtonState();
            }
        }
    }
}
