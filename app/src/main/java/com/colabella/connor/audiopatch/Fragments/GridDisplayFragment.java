package com.colabella.connor.audiopatch.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.RecyclerView.AlbumAndSongAdapter;

import java.util.ArrayList;
import java.util.List;

public class GridDisplayFragment extends Fragment {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView gridView = (RecyclerView) inflater.inflate(R.layout.fragment_album_selection, container, false);
        //readBundle(getArguments(), gridView); // Receives int bundle from fragment initialization and determines which set of data to display via a switch statement
        return gridView;
    }

    private void readBundle(Bundle bundle, RecyclerView gridView) { // Method to receive int bundle from fragment initialization
        if (bundle != null) {
            initializeGridView(gridView, bundle.getInt("key"), bundle); // View gridView, int arguments. Arguments are passed for use in switch statement
        }
    }

    private void initializeGridView(RecyclerView gridView, int arguments, Bundle bundle){
       /* AudioController audioController = new AudioController();

        switch(arguments) { // Instructs fragment whether to display artists or albums
            case 0: { // Display Artists
                gridView.setAdapter(audioController.getArtistAdapter());
            }
            break;
            case 1: { // Display All Albums
                gridView.setAdapter(audioController.getAlbumAdapter()); // Fetches album list, which contains all available albums
            }
            break;
            case 2: { // Display Albums
                String artist = bundle.getString("string");
                final List<List<Audio>> albumList = audioController.getAlbumsByArtist(artist); // List of all albums by selected artist
                List<Audio> songList = new ArrayList<>();

                for (List<Audio> album : albumList) { songList.addAll(album); } // Adds all audio items contained within albums to the songsList

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
                gridView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView

                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                       if(position < albumList.size()) {
                           return 1;
                       }
                       return 2;
                    }
                });

                AlbumAndSongAdapter albumAndSongAdapter = new AlbumAndSongAdapter(albumList, songList);
                gridView.setAdapter(albumAndSongAdapter);
                return;
            }
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        gridView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        */
    }
}
