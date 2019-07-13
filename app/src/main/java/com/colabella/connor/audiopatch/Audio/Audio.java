package com.colabella.connor.audiopatch.Audio;

import android.graphics.Bitmap;
import android.os.Parcel;

public class Audio {

    private String data;
    private String title;
    private String album;       // Not actually being displayed right now, but might implement this later.
    private String artist;
    private String duration;
    private String submitter;
    private boolean selected;   // If the item is currently selected by the RecyclerView
    private Bitmap albumArt;

    public Audio(String data, String title, Bitmap albumArt, String artist, String album, String duration, String submitter, boolean selected) {
        this.data = data;
        this.title = title;
        this.albumArt = albumArt;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.submitter = submitter;
        this.selected = selected;
    }

    protected Audio(Parcel in) {
        data = in.readString();
        title = in.readString();
        album = in.readString();
        artist = in.readString();
        duration = in.readString();
        submitter = in.readString();
        selected = in.readByte() != 0;
        albumArt = in.readParcelable(Bitmap.class.getClassLoader());
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

    public String getArtist() {
        return artist;
    }

    public String getDuration(){
        return duration;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }
}
