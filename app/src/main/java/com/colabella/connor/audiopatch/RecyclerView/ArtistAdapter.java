package com.colabella.connor.audiopatch.RecyclerView;

import android.content.Context;
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
import com.colabella.connor.audiopatch.Controllers.AudioController;
import com.colabella.connor.audiopatch.Fragments.GridDisplayFragment;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private static List<List<List<Audio>>> dataSet = new ArrayList<>();

    public ArtistAdapter() { }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_recyclerview_album_layout, parent,false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AudioController audioController = new AudioController();
        int albumCount = audioController.getAlbumsByArtist(dataSet.get(position).get(0).get(0).getArtist()).size();

        MainActivity mainActivity = new MainActivity();
        Context context = mainActivity.getInstance();
        Locale locale = context.getResources().getConfiguration().locale;
        if(albumCount == 1) {
            String albums = String.format(locale,"%d %s", albumCount, context.getString(R.string.album));
            holder.itemAlbumCount.setText(albums);
        }
        else {
            String albums = String.format(locale,"%d %s", albumCount, context.getString(R.string.albums));
            holder.itemAlbumCount.setText(albums);
        }

        String artist = dataSet.get(position).get(0).get(0).getArtist();
        holder.itemArtist.setText(artist);

        Random r = new Random();
        Bitmap albumArt;
        if(albumCount > 1) {
            final List<List<Audio>> albumList = audioController.getAlbumsByArtist(artist);
            int randomNumber = r.nextInt(albumCount);
            albumArt = albumList.get(randomNumber).get(0).getAlbumArt();
        }
        else {
            albumArt = dataSet.get(position).get(0).get(0).getAlbumArt();
        }

        if (albumArt != null) {
            holder.albumArt.setImageBitmap(albumArt);
        }
        else {
            holder.albumArt.setImageResource(R.drawable.audiopatch_logo_square);
        }

        holder.itemView.setOnClickListener(view -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();

            Bundle bundle = new Bundle();
            bundle.putInt("key", 2);
            String selectedArtist = dataSet.get(position).get(0).get(0).getArtist();
            bundle.putString("selectedArtist", selectedArtist);

            Fragment gridDisplayFragment = new GridDisplayFragment();
            gridDisplayFragment.setArguments(bundle);

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out); // Sets fade in/out animations for transitioning between album selection and song selection screens
            fragmentTransaction.add(R.id.fragment_container, gridDisplayFragment, "FromArtists").addToBackStack("SelectedAlbum");
            fragmentTransaction.commit();
        });
    }

    public void updateDataSet(List<List<List<Audio>>> artistList) {
        dataSet = artistList;
    }

    @Override
    public int getItemCount() {
        if(dataSet != null) {
            return dataSet.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemArtist;
        TextView itemAlbumCount;
        ImageView albumArt;
        View itemPanel;

        ViewHolder(View itemView) {
            super(itemView);
            itemArtist = itemView.findViewById(R.id.album_title);
            itemAlbumCount = itemView.findViewById(R.id.artist);
            albumArt = itemView.findViewById(R.id.album_art);
            itemPanel = itemView.findViewById(R.id.item_panel);
        }
    }
}