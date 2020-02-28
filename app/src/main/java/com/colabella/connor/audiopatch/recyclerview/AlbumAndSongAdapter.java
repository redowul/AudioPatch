package com.colabella.connor.audiopatch.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.controllers.SingletonController;
import com.colabella.connor.audiopatch.controllers.ActivePlaylistController;
import com.colabella.connor.audiopatch.DataRetrievalActivity;
import com.colabella.connor.audiopatch.fragments.SongSelectionFragment;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.nearbyconnections.PayloadController;

import java.util.List;

public class AlbumAndSongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<List<Audio>> albumDataSet;
    private List<Audio> songDataSet;

    public AlbumAndSongAdapter(List<List<Audio>> albumList, List<Audio> songList) {
        if(albumDataSet == null && songDataSet == null) {
            albumDataSet = albumList;
            songDataSet = songList;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_recyclerview_album_layout, parent,false);
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_recyclerview_layout, parent, false);
            return new SongViewHolder(view);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0: { // Albums
                ViewHolder viewHolder = (ViewHolder)holder;

                String albumTitle = albumDataSet.get(position).get(0).getAlbum();
                viewHolder.itemAlbumTitle.setText(albumTitle);

                String artist = albumDataSet.get(position).get(0).getArtist();
                viewHolder.itemArtist.setText(artist);

                Bitmap albumArt = albumDataSet.get(position).get(0).getAlbumArt();
                if (albumArt != null) { viewHolder.albumArt.setImageBitmap(albumArt); }
                else { viewHolder.albumArt.setImageResource(R.drawable.audiopatch_logo_square); }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Fragment songSelectionFragment = new SongSelectionFragment();

                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out); // Sets fade in/out animations for transitioning between album selection and song selection screens
                        fragmentTransaction.add(R.id.fragment_container, songSelectionFragment, "FromSelectedArtist").addToBackStack("SelectedAlbum");
                        fragmentTransaction.commit();

                        Bundle bundle = new Bundle();
                        String selectedAlbumTitle = albumDataSet.get(position).get(0).getAlbum();
                        bundle.putString("albumKey", selectedAlbumTitle);
                        songSelectionFragment.setArguments(bundle);
                    }
                });
                break;
            }
            case 1: { // Songs
                final int songPosition = position - albumDataSet.size();
                SongViewHolder viewHolder = (SongViewHolder)holder;

                String itemTitle = songDataSet.get(songPosition).getTitle();
                viewHolder.itemTitle.setText(itemTitle);

                String artist = songDataSet.get(songPosition).getArtist();
                viewHolder.itemArtist.setText(artist);

                String duration = songDataSet.get(songPosition).getDuration();
                viewHolder.itemDuration.setText(duration);

                Bitmap albumArt = null;
                AlbumAdapter albumAdapter = new AlbumAdapter();
                List<List<Audio>> albumList = albumAdapter.getDataSet();
                for (List<Audio> album: albumList) {
                    if(album.get(0).getAlbum().equalsIgnoreCase(songDataSet.get(songPosition).getAlbum())) {
                        albumArt = album.get(0).getAlbumArt();
                        break;
                    }
                }
                if (albumArt != null) { viewHolder.albumArt.setImageBitmap(albumArt); }
                else { viewHolder.albumArt.setImageResource(R.drawable.audiopatch_logo_square); }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Audio item = songDataSet.get(songPosition);
                        item.setSubmitter(SingletonController.getInstance().getUsername());
                        for (List<Audio> album: albumDataSet) {
                            if(item.getAlbum().equalsIgnoreCase(album.get(0).getAlbum())) {
                                item.setAlbumArt(album.get(0).getAlbumArt());
                                break;
                            }
                        }

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
                });
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) { // Handles setting viewType (albums precede albumDataSet.size, songs are any items remaining)
        if(position < albumDataSet.size()) {
            return 0;
        } // For layout 1
        else {
            return 1;
        }  // For layout 2
    }


    @Override
    public int getItemCount() {
        return albumDataSet.size() + songDataSet.size();
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

    class SongViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        TextView itemArtist;
        TextView itemDuration;
        ImageView albumArt;
        View itemPanel;

        SongViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemArtist = itemView.findViewById(R.id.item_artist);
            itemDuration = itemView.findViewById(R.id.item_duration);
            albumArt = itemView.findViewById(R.id.item_album_art);
            itemPanel = itemView.findViewById(R.id.item_panel);
        }
    }
}