package com.colabella.connor.audiopatch.Controllers;

import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.RecyclerView.ActivePlaylistAdapter;
import com.colabella.connor.audiopatch.RecyclerView.AlbumAdapter;
import com.colabella.connor.audiopatch.RecyclerView.ArtistAdapter;
import com.colabella.connor.audiopatch.RecyclerView.MainDrawerAdapter;
import com.colabella.connor.audiopatch.RecyclerView.SongAdapter;

import java.util.ArrayList;
import java.util.List;

public class SingletonController {

    private static SingletonController instance; // Should ideally be our only static item. All the other variables serve as a reference to this

    private ArrayList<Audio> audioList;
    private ArrayList<List<Audio>> albumList;
    private ArrayList<List<List<Audio>>> artistList;
    private SongAdapter songAdapter;
    private AlbumAdapter albumAdapter;
    private ArtistAdapter artistAdapter;
    private ActivePlaylistAdapter activePlaylistAdapter;
    private MainDrawerAdapter mainDrawerAdapter;
    private boolean isSeekBarTracked; // Handles the draggable seekbar's movement
    private boolean isSeekBarStarted; // Allows for movement of seekpar position before the song has started

    private SingletonController() {
        this.audioList = new ArrayList<>();
        this.albumList = new ArrayList<>();
        this.artistList = new ArrayList<>();
        this.songAdapter = new SongAdapter();
        this.albumAdapter = new AlbumAdapter();
        this.artistAdapter = new ArtistAdapter();
        this.activePlaylistAdapter = new ActivePlaylistAdapter();
        this.mainDrawerAdapter = new MainDrawerAdapter();
        this.isSeekBarTracked = false;
        this.isSeekBarTracked = false;
    }

    public static SingletonController getInstance() {
        if (instance == null) {
            instance = new SingletonController();
        }
        return instance;
    }

    public ArrayList<Audio> getAudioList() {
        return this.audioList;
    }

    public void setAudioList(ArrayList<Audio> audioList) {
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

    public List<List<Audio>> getArtistByArtistName(String artistName) { // returns artist if the artist's name exists in the master artist list
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

    boolean isSeekBarTracked() {
        return isSeekBarTracked;
    }

    void setSeekBarTracked(boolean seekBarTracked) {
        isSeekBarTracked = seekBarTracked;
    }

    boolean isSeekBarStarted() {
        return isSeekBarStarted;
    }

    void setSeekBarStarted(boolean seekBarStarted) {
        isSeekBarStarted = seekBarStarted;
    }

    public MainDrawerAdapter getMainDrawerAdapter() {
        return mainDrawerAdapter;
    }
}

