package com.colabella.connor.audiopatch.Controllers;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioSingleton;
import com.colabella.connor.audiopatch.MainActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Long.valueOf;

public class AudioController {

    public AudioController() { }

    public List<List<Audio>> getAlbumsByArtist(String selectedArtist) {
        List<List<Audio>> albumsBySelectedArtist = new ArrayList<>();
        for (List<Audio> album : AudioSingleton.getInstance().getAlbumList()) {
            if (album.get(0).getArtist().equalsIgnoreCase(selectedArtist)) {
                albumsBySelectedArtist.add(album);
            }
        }
        return albumsBySelectedArtist;
    }

    public void getAudioFilesFromDeviceStorage() {
        RetrieveAudioTask retrieveAudioTask = new RetrieveAudioTask();
        MainActivity mainActivity = new MainActivity();
        Context context = mainActivity.getInstance();
        retrieveAudioTask.execute(context); // Handles album cover retrieval on a secondary thread
    }

    String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}

class RetrieveAudioTask extends AsyncTask<Context, Void, Void> {
    @Override
    // Actual download method, run in the task thread
    protected Void doInBackground(Context... params) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = null;

        if(params[0] != null) {
            cursor = params[0].getContentResolver().query(uri, null, selection, null, sortOrder);
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    if (artist.equals("<unknown>")) {
                        artist = "Unknown artist";
                    }
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    AudioController audioController = new AudioController();
                    int rawDuration = Integer.parseInt(duration);
                    duration = audioController.milliSecondsToTimer(valueOf(rawDuration)); // Sets duration to readable format (##:## rather than the duration in milliseconds, e.g. ######)

                    Audio item = new Audio(data, title, null, artist, album, duration, "User", false, rawDuration); //TODO Replace 'Submitter' field
                    AudioSingleton.getInstance().getAudioList().add(item);
                    AudioSingleton.getInstance().getSongAdapter().updateDataSet(AudioSingleton.getInstance().getAudioList());
                    sortAudioByAlbum(item);
                }
                while (cursor.moveToNext());   // While there are more audio files to be read, continue reading those files
            }
            cursor.close();
        }
        return null;
    }

    @Override
    // Alphabetize the lists. Note that the audio list is alphabetized by default thanks to the sort order of the cursor in getContentResolver()
    protected void onPostExecute(Void param) {
        // Sort artist list alphabetically
        Collections.sort(AudioSingleton.getInstance().getArtistList(), Audio.sortArtistsAlphabeticallyComparator);
        AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList());
        AudioSingleton.getInstance().getArtistAdapter().notifyDataSetChanged();

        // Sort album list alphabetically
        Collections.sort(AudioSingleton.getInstance().getAlbumList(), Audio.sortAlbumsAlphabeticallyComparator);
        AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList());
        AudioSingleton.getInstance().getArtistAdapter().notifyDataSetChanged();
    }

    private void sortAudioByAlbum(Audio item) {
        ArrayList<List<Audio>> albumList = AudioSingleton.getInstance().getAlbumList();
        for (List<Audio> album : albumList) {
            if (album.get(0).getAlbum().equals(item.getAlbum())) { // Only need to check the first item in an album since all item album fields within the same list will match.
                if (album.get(0).getAlbumArt() != null) {
                    item.setAlbumArt(album.get(0).getAlbumArt()); // gets album art from item at 0th index of the album array and uses it to set this audio item's art
                }
                album.add(item);
                AudioSingleton.getInstance().setAlbumList(albumList);
                AudioSingleton.getInstance().getAlbumAdapter().updateDataSet(AudioSingleton.getInstance().getAlbumList()); // update the album adapter
                sortAudioByArtist(album);
                return;
            }
        }
        item.setAlbumArt(getAlbumCover(item.getData()));
        ArrayList<Audio> album = new ArrayList<>(); // creating new album
        album.add(item); // adding item to the new album
        AudioSingleton.getInstance().getAlbumList().add(album); // adding the new album to the album list
        AudioSingleton.getInstance().getAlbumAdapter().updateDataSet(AudioSingleton.getInstance().getAlbumList()); // update the album adapter
        sortAudioByArtist(album);
    }

    private void sortAudioByArtist(List<Audio> album) {
        ArrayList<List<List<Audio>>> artistList = AudioSingleton.getInstance().getArtistList();
        if (artistList.size() > 0) {
            for (int i = 0; i < artistList.size(); i++) {
                if (artistList.get(i).get(0).get(0).getArtist().equals(album.get(0).getArtist())) { // if the artist in the artist list matches the artist of the passed album
                    // iterate through this artist's albums
                    for (int j = 0; j < artistList.get(i).size(); j++) {
                        if (artistList.get(i).get(j).get(0).getAlbum().equals(album.get(0).getAlbum())) {
                            artistList.get(i).set(j, album); // update the album in this artist list
                            AudioSingleton.getInstance().setArtistList(artistList);
                            AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList()); // update the artist adapter
                            return;
                        }
                    }
                    return;
                }
            }
        }
        ArrayList<List<Audio>> artist = new ArrayList<>();
        artist.add(album);
        AudioSingleton.getInstance().getArtistList().add(artist);
        AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList()); // update the artist adapter
    }

    private Bitmap getAlbumCover(String pathId) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        Bitmap albumCover;
        try {
            metaRetriever.setDataSource(pathId);
            byte[] art = metaRetriever.getEmbeddedPicture();
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 0; // Setting this lower returns a lower resolution image. For example, setting this variable to 2 returns an image 1/2 the size of the original. 4 = 1/4 the size, etc.
            albumCover = BitmapFactory.decodeByteArray(art, 0, art.length, opt);
            if (albumCover != null) {
                return albumCover;
            }
        } catch (Exception ignored) { }
        finally {
            metaRetriever.release();
        }
        return null;
    }
}