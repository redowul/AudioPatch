package com.colabella.connor.audiopatch.Audio;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.RecyclerView.AlbumAdapter;
import com.colabella.connor.audiopatch.RecyclerView.ArtistAdapter;
import com.colabella.connor.audiopatch.RecyclerView.SongAdapter;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Long.valueOf;

public class AudioController {

    private List<Audio> audioList;               //todo differentiate between current playlist and audio collection stored on device
    private List<List<Audio>> albumList;
    private List<List<List<Audio>>> artistList;
    private SongAdapter songAdapter = new SongAdapter();
    private AlbumAdapter albumAdapter = new AlbumAdapter();
    private ArtistAdapter artistAdapter = new ArtistAdapter();

    public AudioController() { }

    void setAudioList(List<Audio> list) {
        if(audioList == null) {
            audioList = list;
            //audioList = list;
            songAdapter = new SongAdapter(audioList);
        }
    }

    void setAlbumList(List<List<Audio>> list) {
        if(albumList == null) {
            albumList = list;
            System.out.println(albumList.size());
            //songAdapter = new SongAdapter(audioList);
        }
    }

    public List<List<Audio>> getAlbumList() {
        return albumList;
    }

    public List<List<Audio>> getAlbumsByArtist(String selectedArtist) {
        List<List<Audio>> albumsBySelectedArtist = new ArrayList<>();
        for (List<Audio> album : albumList) {
            if (album.get(0).getArtist().equalsIgnoreCase(selectedArtist)) {
                albumsBySelectedArtist.add(album);
            }
        }
        return albumsBySelectedArtist;
    }

    public SongAdapter getSongAdapter() {
        return songAdapter;
    }

    public AlbumAdapter getAlbumAdapter() {
        return albumAdapter;
    }

    public ArtistAdapter getArtistAdapter() {
        return artistAdapter;
    }

    public List<List<List<Audio>>> getArtistList() {
        return artistList;
    }

    public void getAudioFilesFromDeviceStorage() {
        RetrieveAudioTask retrieveAudioTask = new RetrieveAudioTask();
        MainActivity mainActivity = new MainActivity();
        Context context = mainActivity.getStaticApplicationContext();
        retrieveAudioTask.execute(context); // Handles album cover retrieval on a secondary thread
    }

    /*List<Audio> sortAudioByAlbum(List<Audio> audioList) {
        AlbumCoverTask albumCoverTask = new AlbumCoverTask();
        if(albumList == null) {
            for (Audio item : audioList) {
                if (albumList != null) {
                    // If we find a matching album title, add the given audio to that album's list.
                    for (int i = 0; i < albumList.size(); i++) {
                        if (albumList.get(i).get(0).getAlbum().equals(item.getAlbum())) { // Only need to check the first item in an album since all item album fields within the same list will match.
                            if (albumList.get(i).get(0).getAlbumArt() != null) {
                                item.setAlbumArt(albumList.get(i).get(0).getAlbumArt());
                            }
                            albumList.get(i).add(item);
                            break;
                        } else if (i == albumList.size() - 1) {
                            List<Audio> album = new ArrayList<>();
                            albumCoverTask.execute(item); // Handles album cover retrieval on a secondary thread
                            album.add(item);
                            albumList.add(album);
                            break;
                        }
                    }
                } else {
                    albumList = new ArrayList<>();
                    List<Audio> album = new ArrayList<>();
                    albumCoverTask.execute(item); // Handles album cover retrieval on a secondary thread
                    album.add(item);
                    albumList.add(album);
                }
            }
        }
        return audioList;
    }*/

    /*private List<List<Audio>> sortAudioByAlbum(List<List<Audio>> albumList, Audio item) {
        AlbumCoverTask albumCoverTask = new AlbumCoverTask();
        if (albumList.size() > 0) {
            // If we find a matching album title, add the given audio to that album's list.
            for (int i = 0; i < albumList.size(); i++) {
                if (albumList.get(i).get(0).getAlbum().equals(item.getAlbum())) { // Only need to check the first item in an album since all item album fields within the same list will match.
                    //item.setAlbumArt(albumList.get(i).get(0).getAlbumArt());
                    albumList.get(i).add(item);
                    break;
                } else if (i == albumList.size() - 1) {
                    List<Audio> album = new ArrayList<>();
                    albumCoverTask.execute(item); // Handles album cover retrieval on a secondary thread
                    album.add(item);
                    albumList.add(album);
                    break;
                }
            }
        } else {
            List<Audio> album = new ArrayList<>();
            albumCoverTask.execute(item); // Handles album cover retrieval on a secondary thread
            album.add(item);
            albumList.add(album);
        }
        return albumList;
    }*/

    private List<List<List<Audio>>> sortAudioByArtist(List<List<List<Audio>>> artistList, List<Audio> album) {
        if (artistList.size() > 0) {
            for (int i = 0; i < artistList.size(); i++) {
                if (artistList.size() > 0) {
                    if (artistList.get(i).get(0).get(0).getArtist().equals(album.get(0).getArtist())) {
                        artistList.get(i).add(album);
                        break;
                    } else if (i == artistList.size() - 1) {
                        List<List<Audio>> artist = new ArrayList<>();
                        artist.add(album);
                        artistList.add(artist);
                        break;
                    }
                } else {
                    List<List<Audio>> artist = new ArrayList<>();
                    artist.add(album);
                    artistList.add(artist);
                }
            }
        } else {
            List<List<Audio>> artist = new ArrayList<>();
            artist.add(album);
            artistList.add(artist);
        }
        return artistList;
    }
}

class RetrieveAudioTask extends AsyncTask<Context, Void, Void> {

    private List<Audio> audioList = new ArrayList<>();
    private List<List<Audio>> albumList = new ArrayList<>();

    @Override
    // Actual download method, run in the task thread
    protected Void doInBackground(Context... params) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";

        Cursor cursor = params[0].getContentResolver().query(uri, null, selection, null, null);

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
                    duration = milliSecondsToTimer(valueOf(duration)); // Sets duration to readable format (##:## rather than the duration in milliseconds, e.g. ######)

                    Audio item = new Audio(data, title, null, artist, album, duration, "User", false); //TODO Replace 'Submitter' field
                    item = sortAudioByAlbum(item);
                    audioList.add(item);
                }
                while (cursor.moveToNext());   // While there are more audio files to be read, continue reading those files
            }
            cursor.close();
        }
        return null;
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Void param) {
        AudioController audioController = new AudioController();
        audioController.setAudioList(audioList);
        audioController.setAlbumList(albumList);
    }

    private Audio sortAudioByAlbum(Audio item) {
            if (albumList.size() > 0) {
                // If we find a matching album title, add the given audio to that album's list.
                for (int i = 0; i < albumList.size(); i++) {
                    if (albumList.get(i).get(0).getAlbum().equals(item.getAlbum())) { // Only need to check the first item in an album since all item album fields within the same list will match.
                        if (albumList.get(i).get(0).getAlbumArt() != null) {
                            item.setAlbumArt(albumList.get(i).get(0).getAlbumArt());
                        }
                        albumList.get(i).add(item);
                        break;
                    } else if (i == albumList.size() - 1) {
                        List<Audio> album = new ArrayList<>();
                        item.setAlbumArt(getAlbumCover(item.getData()));
                        album.add(item);
                        albumList.add(album);
                        break;
                    }
                }
            } else {
                List<Audio> album = new ArrayList<>();
                item.setAlbumArt(getAlbumCover(item.getData()));
                album.add(item);
                albumList.add(album);
            }
        return item;
    }

    private Bitmap getAlbumCover(String pathId) {
        System.out.println("Is thing being called?");
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

    private String milliSecondsToTimer(long milliseconds) {
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
/*
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void selectAudioFromStorage(PackageManager packageManager, Activity activity) {
        //If permission has been granted, start the activity.
        if(packageManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, activity.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            Intent searchAudio = new Intent(Intent.ACTION_GET_CONTENT);
            activity.setResult(RESULT_OK, searchAudio);
            searchAudio.setType("audio/*");
            activity.startActivityForResult(searchAudio, 0);
        }
        //Permission has not yet been granted, so we need to request it.
        else{ ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1); }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) //TODO Remove permissions[] if it's going unused?
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults, PackageManager packageManager, Activity activity, Context context) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Since the permission was granted, we invoke a new instance of the process we tried starting before requesting the permission: this time getting through.
                    selectAudioFromStorage(packageManager, activity);
                }
                else {
                    // Permission denied by user
                    Toast.makeText(context, "Permission denied to read your external storage", Toast.LENGTH_SHORT).show();
                }
            }
            break;
           /* case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed
                    // If the user was trying to advertise, begin advertising.
                    if(isAdvertising){
                        Context c = getApplicationContext();
                        n.startAdvertising(c);
                        Toast.makeText(MainActivity.this, "Now advertising.", Toast.LENGTH_SHORT).show();
                    }
                    // If the user was trying to discover, begin discovery.
                    else if(isDiscovering){
                        Context c = getApplicationContext();
                        n.startDiscovery(c);
                        Toast.makeText(MainActivity.this, "Searching for devices.", Toast.LENGTH_SHORT).show();
                    }
                    savedItem.setChecked(true);
                }
                else {
                    // permission denied
                    isAdvertising = false;
                    isDiscovering = false;
                    Toast.makeText(MainActivity.this, "Permission denied to access your device's location.", Toast.LENGTH_SHORT).show();
                    //closeNow();
                }
            }
        }
    }

    // Creates a new Audio object and sends it to the RecyclerView
    public void onActivityResult(Intent resultData, Context context) {
        Uri uri = resultData.getData();
        AudioDataParser audioDataParser = new AudioDataParser();

        Audio audio = createAudio(uri,                                    // Used for album cover
                audioDataParser.getFileData(uri, context, 1), // Filename
                audioDataParser.getFileData(uri, context, 2), // Artist
                audioDataParser.getFileData(uri, context, 3), // File's duration in milliseconds
                audioDataParser.getFileData(uri, context, 4), //TODO Returns nickname of submitter
                context);

        audioList.add(audio);
        recyclerViewAdapter.notifyDataSetChanged();

        // Function enabling guests to send audio to host devices. Only called if (isGuest == true),
        // which is set to true only after successfully connecting as a guest to a host device.
        //    String endpointId = n.getEndpointId();
        /*if (n.getIsGuest()) {
            try {
                // Calls select method, which enables user to select an audio file to send to the host.
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uriSound, "r");
                Payload filePayload = Payload.fromFile(pfd);

                // Construct a simple message mapping the ID of the file payload to the desired filename.
                //Messages sent in this context are the name of the file and the nickname of the submitter.
                String payloadFilenameMessage = filePayload.getId() + ":" + a.getFileData(uriSound, c, 1) + ":" + getNickName();
                //String payloadSubmitterNickname = filePayload.getId() + "::" + getNickName();
                //String payloadFilenameMessage = filePayload.getId() + ":" + a.getTitle(uriSound);

                // Send data as a bytes payload.
                Nearby.getConnectionsClient(MainActivity.this).sendPayload(endpointId, Payload.fromBytes(payloadFilenameMessage.getBytes(UTF_8)));
                //Nearby.getConnectionsClient(MainActivity.this).sendPayload(endpointId, Payload.fromBytes(payloadSubmitterNickname.getBytes(UTF_8)));

                // Finally, send the file payload.
                Nearby.getConnectionsClient(MainActivity.this).sendPayload(endpointId, filePayload);
            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "File not found.", Toast.LENGTH_SHORT).show();
            }
        }
    }
*/