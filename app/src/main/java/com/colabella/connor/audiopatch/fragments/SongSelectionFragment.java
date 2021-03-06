package com.colabella.connor.audiopatch.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.controllers.SingletonController;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.recyclerview.AlbumListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SongSelectionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_song_selection, container, false);

        Bundle bundle = this.getArguments(); // Accepts passed album title from AlbumSelectionFragment. Since we're only passing a string, the value is small enough to avoid a TransactionTooLarge Exception.
        if (bundle != null) {
            List<Audio> selectedAlbum = new ArrayList<>();
            String selectedAlbumTitle = bundle.getString("albumKey");

            List<List<Audio>> albums = SingletonController.getInstance().getAlbumList();
            for (List<Audio> album : albums) {
                if (album.get(0).getAlbum().equalsIgnoreCase(selectedAlbumTitle)) {
                    selectedAlbum = album;
                    break;
                }
            }

            ImageView imageView = view.findViewById(R.id.album_art);
            Bitmap albumArt = selectedAlbum.get(0).getAlbumArt(); // If selectedAlbum != null, alBumArt = selectedAlbum.getAlbumArt(). Else set albumArt to null.
            if (albumArt != null) { imageView.setImageBitmap(albumArt); }
            else { imageView.setImageResource(R.drawable.audiopatch_logo_square); }

            AlbumListViewAdapter audioListViewRecyclerViewAdapter = new AlbumListViewAdapter(selectedAlbum);

            // Passes position of selected album within master album list to the RecyclerView
            audioListViewRecyclerViewAdapter.setDataSet(selectedAlbum);

            TextView artistLabel = view.findViewById(R.id.artist_label);
            String albumName = selectedAlbum.get(0).getAlbum();
            if(albumName != null) { artistLabel.setText(albumName); }

            RecyclerView recyclerView = view.findViewById(R.id.songSelectionRecyclerView);
            recyclerView.setNestedScrollingEnabled(false); // Ensures recyclerView can overScroll, and doesn't get stuck inside of the nestedScrollView. (Allows view to scroll past where finger stops.)
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(audioListViewRecyclerViewAdapter);
        }
        return view;
    }
}
