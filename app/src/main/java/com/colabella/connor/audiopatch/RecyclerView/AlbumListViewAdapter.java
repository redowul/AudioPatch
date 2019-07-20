package com.colabella.connor.audiopatch.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import java.util.List;

public class AlbumListViewAdapter extends RecyclerView.Adapter<AlbumListViewAdapter.ViewHolder> {
    private static List<Audio> dataSet;

    public void setDataSet(List<Audio> selectedAlbum) { // Sets RecyclerView data
        dataSet = selectedAlbum;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_recyclerview_song_layout, parent, false);
        return new AlbumListViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = dataSet.get(position).getTitle();
        holder.itemTitle.setText(title);
        String artist = dataSet.get(position).getArtist();
        holder.itemArtist.setText(artist);
        String duration = dataSet.get(position).getDuration();
        holder.itemDuration.setText(duration);
        String itemNumber = Integer.toString(position + 1);
        holder.itemNumber.setText(itemNumber);
    }

    @Override
    public int getItemCount() {
        if(dataSet != null) { return dataSet.size(); }
        return 0;
    }

    /******** View Holder Class*/
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView itemTitle;
        private TextView itemArtist;
        private TextView itemDuration;
        private TextView itemNumber;
        private View itemPanel;

        private ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemArtist = itemView.findViewById(R.id.item_artist);
            itemDuration = itemView.findViewById(R.id.item_duration);
            itemNumber = itemView.findViewById(R.id.item_number);
            itemPanel = itemView.findViewById(R.id.item_panel);
            itemPanel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            Audio audioData = dataSet.get(position);
            Uri uri = Uri.parse(audioData.getData());
            MainActivity mainActivity = new MainActivity();
            Context context = mainActivity.getStaticApplicationContext();
            AudioController audioController = new AudioController();
            audioController.playSelectedAudio(context, uri);
        }
    }
}