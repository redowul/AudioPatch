package com.colabella.connor.audiopatch.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.DataRetrievalActivity;
import com.colabella.connor.audiopatch.Fragments.SongSelectionFragment;
import com.colabella.connor.audiopatch.R;
import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private static List<List<Audio>> dataSet;

    public AlbumAdapter() { }

    List<List<Audio>> getDataSet() {
        return dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_recyclerview_album_layout, parent,false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(dataSet != null ) {
            String albumTitle = dataSet.get(position).get(0).getAlbum();
            holder.itemAlbumTitle.setText(albumTitle);

            String artist = dataSet.get(position).get(0).getArtist();
            holder.itemArtist.setText(artist);

            Bitmap albumArt = dataSet.get(position).get(0).getAlbumArt();
            if (albumArt != null) {
                holder.albumArt.setImageBitmap(albumArt);
            } else {
                holder.albumArt.setImageResource(R.drawable.audiopatchlogosquare);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment songSelectionFragment = new SongSelectionFragment();

                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out); // Sets fade in/out animations for transitioning between album selection and song selection screens
                    fragmentTransaction.add(R.id.fragment_container, songSelectionFragment, "FromAlbums").addToBackStack("SelectedAlbum"); //fragment_container is the container in activity_dataretriever?
                    fragmentTransaction.commit();

                    Bundle bundle = new Bundle();
                    String selectedAlbumTitle = dataSet.get(position).get(0).getAlbum();
                    bundle.putString("albumKey", selectedAlbumTitle);
                    songSelectionFragment.setArguments(bundle);
                }
            });
        }
    }

    public void updateDataSet(List<List<Audio>> albumList) {
        dataSet = albumList;
    }

    @Override
    public int getItemCount() {
        if(dataSet != null) {
            return dataSet.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemAlbumTitle;
        TextView itemArtist;
        ImageView albumArt;
        View itemPanel;

        ViewHolder(View itemView) {
            super(itemView);
            itemAlbumTitle = itemView.findViewById(R.id.album_title);
            itemArtist = itemView.findViewById(R.id.artist);
            albumArt = itemView.findViewById(R.id.album_art);
            itemPanel = itemView.findViewById(R.id.item_panel);
        }
    }
}