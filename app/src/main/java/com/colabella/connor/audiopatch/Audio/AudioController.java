package com.colabella.connor.audiopatch.Audio;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;
import android.widget.Toast;

import com.colabella.connor.audiopatch.Controller;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.RecyclerView.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AudioController {
    private static List<Audio> audioList = new ArrayList<>();
    private static RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
    private static MediaPlayer mediaPlayer = null;

    public List<Audio> getAudioList() { return audioList; }
    public void setAudioList(List<Audio> audioList) { AudioController.audioList = audioList; }

    public RecyclerViewAdapter getRecyclerViewAdapter() { return recyclerViewAdapter; }
    public void setRecyclerViewAdapter(RecyclerViewAdapter recyclerViewAdapter) { AudioController.recyclerViewAdapter = recyclerViewAdapter; }

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }
    public void setMediaPlayer(MediaPlayer player) { AudioController.mediaPlayer = player; }

    public Audio getSelectedAudio() {
        List<Audio> audioList = AudioController.audioList;
        for(Audio item : audioList){
            if(item.getSelected()){ // If the item is selected, return it
                return item;
            }
        }
        return null;                // If there's no item selected, return null.
    }

    // Sets selected item as true and the rest as false. Those booleans are then referenced by onBindViewHolder in RecyclerViewAdapter to determine which items to highlight.
    public void setSelectedAudio(int index){
        Controller c = new Controller();
        if (c.getUser().getRecyclerViewPermission()) {
            List<Audio> audioList = getAudioList();
            for (int i = 0; i < audioList.size(); i++) {    // Set all the items in the list to false.
                audioList.get(i).setSelected(false); }
            audioList.get(index).setSelected(true);         // Now that all items are false, set the one we want selected to true.
            setAudioList(audioList);                        // Update the global audioList state
        }
    }

    public void playSelectedAudio(Context context, Uri uri){
        if(mediaPlayer == null){
           if(context != null && uri != null) {
               mediaPlayer = MediaPlayer.create(context, uri);
               mediaPlayer.start();
           }
        }
        else {   // MediaPlayer is not null
            if (mediaPlayer.isPlaying()) { mediaPlayer.pause(); }
            mediaPlayer.stop();
            mediaPlayer.release();

            mediaPlayer = MediaPlayer.create(context, uri);
            mediaPlayer.start();
        }
    }

    public void releaseSelectedAudio(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;     // Reset mediaPlayer to its default state to protect from null pointer exception error
        }
    }

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
            }*/
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
        }*/
    }

    private Audio createAudio(Uri uri, String fileName, String artist, String duration, String submitter, Context context){
        MediaMetadataRetriever myRetriever = new MediaMetadataRetriever();
        myRetriever.setDataSource(context, uri); // the URI of audio file

        return new Audio(uri.toString(), fileName, myRetriever, artist, duration, submitter, false);
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
        }
        else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
