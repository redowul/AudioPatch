package com.colabella.connor.audiopatch.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.controllers.SingletonController;
import com.colabella.connor.audiopatch.DataRetrievalActivity;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.nearbyconnections.PayloadController;

import java.util.ArrayList;
import java.util.List;

public class AlbumListViewAdapter extends RecyclerView.Adapter<AlbumListViewAdapter.ViewHolder> {
    private static List<Audio> dataSet = new ArrayList<>();

    public AlbumListViewAdapter(List<Audio> selectedAlbum) {
        if(dataSet != null) {
            if (dataSet.size() == 0) {
                dataSet = selectedAlbum;
            }
        }
    }

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
        int number = position + 1;
        String itemNumber;
        if(number <= 99) {
           itemNumber = Integer.toString(position + 1);
        }
        else {
            itemNumber = "99+"; // Needed because the design can't fit any song positions higher than 99 without pushing other text off the screen
        }
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

        private ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemArtist = itemView.findViewById(R.id.item_artist);
            itemDuration = itemView.findViewById(R.id.item_duration);
            itemNumber = itemView.findViewById(R.id.item_number);
            View itemPanel = itemView.findViewById(R.id.item_panel);
            itemPanel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Audio item = dataSet.get(this.getAdapterPosition());
            item.setSubmitter(SingletonController.getInstance().getUsername());
            if(dataSet.get(0).getAlbumArt() != null) { item.setAlbumArt(dataSet.get(0).getAlbumArt()); }

            DataRetrievalActivity dataRetrievalActivity = new DataRetrievalActivity();
            dataRetrievalActivity.endActivity();

            PayloadController payloadController = new PayloadController();
            if(SingletonController.getInstance().getEndpointIdList() != null) {
                if (SingletonController.getInstance().getEndpointIdList().size() > 0) {
                    if(SingletonController.getInstance().isGuest()) {
                        String endpointId = SingletonController.getInstance().getEndpointIdList().get(0);

                        MainActivity mainActivity = new MainActivity();
                        Context context = mainActivity.getInstance();

                        payloadController.sendAudio(endpointId, item, context);
                        return;
                    }
                }
            }
            ActivePlaylistAdapter activePlaylistAdapter = SingletonController.getInstance().getActivePlaylistAdapter();
            activePlaylistAdapter.addItem(Audio.copy(item));
            activePlaylistAdapter.notifyDataSetChanged();
        }
    }
}
