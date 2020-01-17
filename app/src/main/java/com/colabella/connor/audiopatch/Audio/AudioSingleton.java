package com.colabella.connor.audiopatch.Audio;

import com.colabella.connor.audiopatch.RecyclerView.ActivePlaylistAdapter;
import com.colabella.connor.audiopatch.RecyclerView.AlbumAdapter;
import com.colabella.connor.audiopatch.RecyclerView.ArtistAdapter;
import com.colabella.connor.audiopatch.RecyclerView.SongAdapter;
import java.util.ArrayList;
import java.util.List;

public class AudioSingleton {

    private static AudioSingleton instance; // Should ideally be our only static item. All the other variables serve as a reference to this

    private ArrayList<Audio> audioList;
    private ArrayList<List<Audio>> albumList;
    private ArrayList<List<List<Audio>>> artistList;
    private SongAdapter songAdapter;
    private AlbumAdapter albumAdapter;
    private ArtistAdapter artistAdapter;
    private ActivePlaylistAdapter activePlaylistAdapter;
    private boolean isSeekBarTracked; // Handles the draggable seekbar's movement

    private AudioSingleton() {
        this.audioList = new ArrayList<>();
        this.albumList = new ArrayList<>();
        this.artistList = new ArrayList<>();
        this.songAdapter = new SongAdapter();
        this.albumAdapter = new AlbumAdapter();
        this.artistAdapter = new ArtistAdapter();
        this.activePlaylistAdapter = new ActivePlaylistAdapter();
        this.isSeekBarTracked = false;
    }

    public static AudioSingleton getInstance() {
        if (instance == null) {
            instance = new AudioSingleton();
        }
        return instance;
    }

    public ArrayList<Audio> getAudioList() {
        return this.audioList;
    }

    void setAudioList(ArrayList<Audio> audioList) {
        this.audioList = audioList;
    }

    public ArrayList<List<Audio>> getAlbumList() {
        return this.albumList;
    }

    void setAlbumList(ArrayList<List<Audio>> albumList) {
        this.albumList = albumList;
    }

    public SongAdapter getSongAdapter() {
        return songAdapter;
    }

    public AlbumAdapter getAlbumAdapter() {
        return albumAdapter;
    }

    public ArrayList<List<List<Audio>>> getArtistList() {
        return artistList;
    }

    void setArtistList(ArrayList<List<List<Audio>>> artistList) {
        this.artistList = artistList;
    }

    public ArtistAdapter getArtistAdapter() {
        return artistAdapter;
    }

    public List<Audio> getAlbumByAlbumTitle(String albumTitle) { // returns album if the album title exists in the master album list
        for (List<Audio> album : this.albumList) {
            if (album.get(0).getAlbum().equalsIgnoreCase(albumTitle)) {
                return album;
            }
        }
        return null;
    }

    public List<List<Audio>> getArtistByArtistName (String artistName) { // returns artist if the artist's name exists in the master artist list
        for (List<List<Audio>> artist : this.artistList) {
            if (artist.get(0).get(0).getArtist().equalsIgnoreCase(artistName)) {
                return artist;
            }
        }
        return null;
    }

    public ActivePlaylistAdapter getActivePlaylistAdapter() {
        return activePlaylistAdapter;
    }

    public void setActivePlaylistAdapter(ActivePlaylistAdapter activePlaylistAdapter) {
        this.activePlaylistAdapter = activePlaylistAdapter;
    }

    public boolean isSeekBarTracked() {
        return isSeekBarTracked;
    }

    public void setSeekBarTracked(boolean seekBarTracked) {
        isSeekBarTracked = seekBarTracked;
    }
}

