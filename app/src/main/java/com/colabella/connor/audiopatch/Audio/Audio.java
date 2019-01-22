package com.colabella.connor.audiopatch.Audio;

import android.media.MediaMetadataRetriever;

public class Audio {

    private String data;
    private String title;
    private String album;       // Not actually being displayed right now, but might implement this later.
    private String artist;
    private String duration;
    private String submitter;
    private boolean selected;   // If the item is currently selected by the RecyclerView
    private MediaMetadataRetriever albumArt;

    public Audio(String title, boolean selected){
        this.title = title;
        this.selected = selected;
    }

    public Audio(String data, String title, MediaMetadataRetriever albumArt, String artist, String duration, String submitter, boolean selected) {
        this.data = data;
        this.title = title;
        this.albumArt = albumArt;
        this.artist = artist;
        this.duration = duration;
        this.submitter = submitter;
        this.selected = selected;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public MediaMetadataRetriever getAlbumArt() {
        return albumArt;
    }
}
