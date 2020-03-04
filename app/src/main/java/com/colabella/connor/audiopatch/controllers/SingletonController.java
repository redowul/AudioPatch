package com.colabella.connor.audiopatch.controllers;

import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.recyclerview.ActivePlaylistAdapter;
import com.colabella.connor.audiopatch.recyclerview.AlbumAdapter;
import com.colabella.connor.audiopatch.recyclerview.ArtistAdapter;
import com.colabella.connor.audiopatch.recyclerview.MainDrawerAdapter;
import com.colabella.connor.audiopatch.recyclerview.SongAdapter;

import java.util.ArrayList;
import java.util.List;

public class SingletonController {

    private static SingletonController instance; // Should ideally be our only static item. All the other variables serve as a reference to this

    private ArrayList<Audio> audioList;
    private ArrayList<List<Audio>> albumList;
    private ArrayList<List<List<Audio>>> artistList;
    private ArrayList<String> endpointIdList;
    private SongAdapter songAdapter;
    private AlbumAdapter albumAdapter;
    private ArtistAdapter artistAdapter;
    private ActivePlaylistAdapter activePlaylistAdapter;
    private MainDrawerAdapter mainDrawerAdapter;
    private boolean isSeekBarTracked; // Handles the draggable seekbar's movement
    private boolean isSeekBarStarted; // Allows for movement of seekpar position before the song has started
    private boolean isGuest;
    private boolean itemSelected;
    private String username;
    private String filter;
    private Audio selectedAudio;

    private SingletonController() {
        this.audioList = new ArrayList<>();
        this.albumList = new ArrayList<>();
        this.artistList = new ArrayList<>();
        this.endpointIdList = new ArrayList<>();
        this.songAdapter = new SongAdapter();
        this.albumAdapter = new AlbumAdapter();
        this.artistAdapter = new ArtistAdapter();
        this.activePlaylistAdapter = new ActivePlaylistAdapter();
        this.mainDrawerAdapter = new MainDrawerAdapter();
        this.isSeekBarTracked = false;
        this.isSeekBarTracked = false;
        this.isGuest = false;
        this.itemSelected = false;
        this.username = android.os.Build.MODEL;
        this.filter = null;
        this.selectedAudio = null;
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

    public ArrayList<String> getEndpointIdList() {
        return endpointIdList;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        if(!filter.equals("")) {
            this.filter = filter;
        }
        else {
            this.filter = null;
        }
    }

    public Audio getSelectedAudio() {
        return this.selectedAudio;
    }

    public void setSelectedAudio(Audio selectedAudio) {
        this.selectedAudio = selectedAudio;
    }

    public boolean isItemSelected() {
        return itemSelected;
    }

    public void setItemSelected(boolean itemSelected) {
        this.itemSelected = itemSelected;
    }
}

