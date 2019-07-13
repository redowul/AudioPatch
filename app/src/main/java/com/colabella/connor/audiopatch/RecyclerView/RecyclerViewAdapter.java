package com.colabella.connor.audiopatch.RecyclerView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements SwipeAndDragHelper.ActionCompletionContract{
    private ItemTouchHelper itemTouchHelper;

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_layout,parent,false);
        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, int position) {
        AudioController audioController = new AudioController();
        Audio audio = audioController.getAudioList().get(position);
        String title = audio.getTitle();
        holder.itemTitle.setText(title);

        String artist = audio.getArtist();
        holder.itemArtist.setText(artist);

        String duration = audio.getDuration();
        holder.itemDuration.setText(duration);

        String submitter = audio.getSubmitter();
        //TODO change this back when usernames are implemented
        //holder.itemSubmitter.setText(submitter);

      /*  byte[] artwork = null;
        MediaMetadataRetriever myRetriever = audio.getAlbumArt();
        if (myRetriever != null) { artwork = myRetriever.getEmbeddedPicture(); }
        if(artwork != null){
            holder.albumArt.setImageResource(0);
            Bitmap bMap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
            holder.albumArt.setImageBitmap(bMap);
            holder.itemSubmitter.setText("Passed");
        }
        else {
            holder.albumArt.setImageBitmap(null);
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
        if(audioController.getAudioList().get(position).getSelected()){ // If isSelected returns true, highlight the item.
            holder.itemView.setBackgroundResource(R.color.recyclerViewAccent);
        }
        Controller controller = new Controller();
        if (controller.getUser().getRecyclerViewPermission()) {
            holder.itemView.findViewById(R.id.item_handle).setVisibility(View.VISIBLE);
        }
        else { holder.itemView.findViewById(R.id.item_handle).setVisibility(View.GONE); }
        */
    }

    @Override
    public int getItemCount() {
        AudioController audioController = new AudioController();
        return audioController.getAudioList().size();
    }

    @Override
    public void onViewMoved(int fromPosition, int toPosition) {
      /*  AudioController audioController = new AudioController();
        List<Audio> audioList = audioController.getAudioList();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(audioList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(audioList, i, i - 1);
            }
        }
        audioController.setAudioList(audioList);
        notifyItemMoved(fromPosition, toPosition);
        */
    }

    @Override
    public void onViewSwiped(int position) {
      /*  Controller controller = new Controller();
        RecyclerViewController recyclerViewController = new RecyclerViewController();
        AudioController audioController = new AudioController();

        List<Audio> audioList = audioController.getAudioList();
        if (controller.getUser().getRecyclerViewPermission()) {
            for(int i = 0; i < audioList.size(); i++) {
                if (audioList.get(i).getSelected() && i == position) {
                    audioController.releaseSelectedAudio();                           // Releases selected audio from the MediaPlayer
                    recyclerViewController.togglePlayButtonState();
                    break;
                }
            }

            audioList.remove(position);
            audioController.setAudioList(audioList);
            notifyItemRemoved(position);
        }
        */
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
        /*    Controller controller = new Controller();
            AudioController audioController = new AudioController();
            RecyclerViewController recyclerViewController = new RecyclerViewController();
            if (controller.getUser().getRecyclerViewPermission()) {
                audioController.setSelectedAudio(getAdapterPosition());             // Set item at clicked position's isClicked to true
                for (int i = 0; i < getItemCount(); i++) { notifyItemChanged(i); }
                recyclerViewController.playSelectedItem();
                recyclerViewController.togglePlayButtonState();
            }
            */
        }
    }
}
