package com.colabella.connor.audiopatch.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.DataRetrievalActivity;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private static List<Audio> dataSet;

    public SongAdapter() { }

    public SongAdapter(List<Audio> audioList) {
        dataSet = audioList;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_recyclerview_layout, parent,false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String itemTitle = dataSet.get(position).getTitle();
        holder.itemTitle.setText(itemTitle);

        String artist = dataSet.get(position).getArtist();
        holder.itemArtist.setText(artist);

        String duration = dataSet.get(position).getDuration();
        holder.itemDuration.setText(duration);

        Bitmap albumArt = dataSet.get(position).getAlbumArt();
        AudioController audioController = new AudioController();
        final List<List<Audio>> albumList = audioController.getAlbumList();
        for (List<Audio> album: albumList) {
            if(album.get(0).getAlbum().equalsIgnoreCase(dataSet.get(position).getAlbum())) {
                albumArt = album.get(0).getAlbumArt();
                break;
            }
        }
        if (albumArt != null) { holder.albumArt.setImageBitmap(albumArt); }
        else { holder.albumArt.setImageResource(R.drawable.audiopatchlogosquare); }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivePlaylistController activePlaylistController = new ActivePlaylistController();
                Audio item = dataSet.get(position);
                for (List<Audio> album: albumList) {
                    if(item.getAlbum().equalsIgnoreCase(album.get(0).getAlbum())) {
                       item.setAlbumArt(album.get(0).getAlbumArt());
                       break;
                    }
                }

                ActivePlaylistAdapter activePlaylistAdapter = activePlaylistController.getActivePlaylistAdapter();
                activePlaylistAdapter.addItem(item);
                activePlaylistAdapter.notifyDataSetChanged();
                DataRetrievalActivity dataRetrievalActivity = new DataRetrievalActivity();
                dataRetrievalActivity.endActivity();
            }
        });
    }

    public void updateDataSet(List<Audio> audioList) {
        dataSet = audioList;
        AudioController audioController = new AudioController();
        audioController.getSongAdapter().notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        TextView itemArtist;
        TextView itemDuration;
        ImageView albumArt;
        View itemPanel;

        ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemArtist = itemView.findViewById(R.id.item_artist);
            itemDuration = itemView.findViewById(R.id.item_duration);
            albumArt = itemView.findViewById(R.id.item_album_art);
            itemPanel = itemView.findViewById(R.id.item_panel);
        }
    }
}