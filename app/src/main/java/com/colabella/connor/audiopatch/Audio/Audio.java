package com.colabella.connor.audiopatch.Audio;

import android.graphics.Bitmap;
import android.os.Parcel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Audio {

    private String data;
    private String title;
    private String album;
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

    public Audio(String title, boolean selected) {
        this.title = title;
        this.selected = selected;
    }

    public Audio(Bitmap albumArt, String album) {
        this.albumArt = albumArt;
        this.album = album;
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

    public boolean isSelected() {
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

    public String getDuration() {
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

    /**
     * Comparators for audio
     */
    public static Comparator<Audio> sortAudioAlphabeticallyComparator = new Comparator<Audio>() {
        @Override
        public int compare(Audio a1, Audio a2) {
            return a1.getTitle().compareTo(a2.getTitle());
        }
    };

    public static Comparator<Audio> sortAudioOmegapsicallyComparator = new Comparator<Audio>() {
        @Override
        public int compare(Audio a1, Audio a2) {
            return a2.getTitle().compareTo(a1.getTitle());
        }
    };

    public static void sortAudioByArtist() {
        Collections.sort(AudioSingleton.getInstance().getAudioList(), Audio.sortAudioAlphabeticallyComparator);
        Collections.sort(AudioSingleton.getInstance().getAudioList(), Audio.sortAudioByArtistsComparator);
        String currentArtist = null;
        ArrayList<Audio> masterAudioList = new ArrayList<>();
        ArrayList<Audio> currentAudioList = new ArrayList<>();

        for (Audio item : AudioSingleton.getInstance().getAudioList()) {
            if (currentArtist == null) {
                currentArtist = item.getArtist();
                currentAudioList.add(item);
            }
            else if (item.getArtist().equals(currentArtist)) {
                currentAudioList.add(item);
            }
            else {
                Collections.sort(currentAudioList, Audio.sortAudioByAlbumsComparator);
                masterAudioList.addAll(currentAudioList);
                currentAudioList.clear();
                currentArtist = item.getArtist();
                currentAudioList.add(item);
            }
        }
        AudioSingleton.getInstance().setAudioList(masterAudioList);
    }

    public static void sortAudioByAlbum() {
        Collections.sort(AudioSingleton.getInstance().getAudioList(), Audio.sortAudioByArtistsComparator); //TODO can maybe remove this line
        Collections.sort(AudioSingleton.getInstance().getAudioList(), Audio.sortAudioByAlbumsComparator);
    }

    private static Comparator<Audio> sortAudioByAlbumsComparator = new Comparator<Audio>() {
        @Override
        public int compare(Audio a1, Audio a2) {
            return a1.getAlbum().compareTo(a2.getAlbum());
        }
    };

    private static Comparator<Audio> sortAudioByArtistsComparator = new Comparator<Audio>() {
        @Override
        public int compare(Audio a1, Audio a2) {
            return a1.getArtist().compareTo(a2.getArtist());
        }
    };

    public static Comparator<Audio> sortAudioByShortestDurationComparator = new Comparator<Audio>() {
        @Override
        public int compare(Audio a1, Audio a2) {
            return a1.getDuration().compareTo(a2.getDuration());
        }
    };

    public static Comparator<Audio> sortAudioByLongestDurationComparator = new Comparator<Audio>() {
        @Override
        public int compare(Audio a1, Audio a2) {
            return a2.getDuration().compareTo(a1.getDuration());
        }
    };

    /**
     * Comparators for albums
     */
    public static Comparator<List<Audio>> sortAlbumsAlphabeticallyComparator = new Comparator<List<Audio>>() {
        @Override
        public int compare(List<Audio> a1, List<Audio> a2) {
            return a1.get(0).getAlbum().compareTo(a2.get(0).getAlbum());
        }
    };

    public static Comparator<List<Audio>> sortAlbumsOmegapsicallyComparator = new Comparator<List<Audio>>() {
        @Override
        public int compare(List<Audio> a1, List<Audio> a2) {
            return a2.get(0).getAlbum().compareTo(a1.get(0).getAlbum());
        }
    };

    public static Comparator<List<Audio>> sortAlbumsByArtistsComparator = new Comparator<List<Audio>>() {
        @Override
        public int compare(List<Audio> a1, List<Audio> a2) {
            return a1.get(0).getArtist().compareTo(a2.get(0).getArtist());
        }
    };

    /**
     * Comparators for artists
     */
    public static Comparator<List<List<Audio>>> sortArtistsAlphabeticallyComparator = new Comparator<List<List<Audio>>>() { //TODO Artist comparators not working
        @Override
        public int compare(List<List<Audio>> a1, List<List<Audio>> a2){
            return a1.get(0).get(0).getArtist().compareTo(a2.get(0).get(0).getArtist());
        }
    };

    public static Comparator<List<List<Audio>>> sortArtistsOmegapsicallyComparator = new Comparator<List<List<Audio>>>() {
        @Override
        public int compare(List<List<Audio>> a1, List<List<Audio>> a2) {
            return a2.get(0).get(0).getArtist().compareTo(a1.get(0).get(0).getArtist());
        }
    };

    // Uses the getAlbumsByArtist method to fetch the total albums associated with each artist, then compares the two numbers.
    public static Comparator<List<List<Audio>>> sortArtistsByTotalAlbumsComparator = new Comparator<List<List<Audio>>>() {
        @Override
        public int compare(List<List<Audio>> a1, List<List<Audio>> a2) {
            AudioController audioController = new AudioController();
            Integer list1 = audioController.getAlbumsByArtist(a1.get(0).get(0).getArtist()).size();
            Integer list2 = audioController.getAlbumsByArtist(a2.get(0).get(0).getArtist()).size();
            return list2.compareTo(list1);
        }
    };
}
