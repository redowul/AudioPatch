package com.colabella.connor.audiopatch.Controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioSingleton;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.RecyclerView.ActivePlaylistAdapter;

public class ActivePlaylistController extends ActivePlaylistAdapter {

    /**
     * Methods related to the Media Player
     */

    private static MediaPlayer mediaPlayer;

    public MediaPlayer getMediaPlayer() {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        return mediaPlayer;
    }

    public void startMediaPlayer() {
        BottomSheetController bottomSheetController = new BottomSheetController();
        bottomSheetController.initializeSeekBar();
        mediaPlayer.start();
    }

    public void initializeMediaPlayer(Audio selectedItem) {
        if (selectedItem != null) {                                  // avoids null pointer exception.
            MainActivity mainActivity = new MainActivity();
            Context context = mainActivity.getInstance();

            BottomSheetController bottomSheetController = new BottomSheetController();
            bottomSheetController.alterBottomSheet(selectedItem); // applies song data to bottom sheet

            String audioToPlayString = selectedItem.getData();  // Convert Audio to String
            Uri uri = Uri.parse(audioToPlayString);             // Convert to Uri by parsing the String

            if (context != null && uri != null) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                else if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = MediaPlayer.create(context, uri);
                mediaPlayer.setOnCompletionListener(mp -> {
                    int currentlySelectedItemIndex = getCurrentlySelectedItemIndex();
                    Audio currentlySelectedItem;
                    if (currentlySelectedItemIndex == getItemCount() - 1) { // if song played was last in the list
                        setSelectedAudio(0);
                        currentlySelectedItem = getSelectedAudio();
                        initializeMediaPlayer(currentlySelectedItem);

                        SeekBar seekbar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_seekbar);
                        seekbar.setProgress(0);
                        togglePlayButtonState();
                        // TODO if playlist looping is enabled
                        // if() {

                        //  }
                    } else { // item isn't the last item, so we play it
                        setSelectedAudio(getCurrentlySelectedItemIndex() + 1);
                        currentlySelectedItem = getSelectedAudio();
                        initializeMediaPlayer(currentlySelectedItem);
                        startMediaPlayer();
                    }
                });
                showNotification(selectedItem);
                TextView audioLength = mainActivity.getInstance().findViewById(R.id.audio_length); // textView of the duration of the current audio file
                audioLength.setText(selectedItem.getDuration()); // setting the duration
            }
        }
    }

    // Determines which button on the bottom toolbar was pressed
    public void determineButtonSelected(String buttonIdString, View view) {
        switch (buttonIdString) {
            case "back_button": {
                playPreviousItem(); // Moves current selection to the previous available item in the RecyclerView. Selects the last item in the list if pressed at index 0.
                break;
            }
            case "play_button": {
                if (mediaPlayer == null) { // mediaPlayer is null
                    if (getItemCount() > 0) {
                        mediaPlayer = new MediaPlayer();
                        boolean isItemSelected = isItemSelected();
                        if(isItemSelected) {
                            //int selectedItem = AudioSingleton.getInstance().getActivePlaylistAdapter().getCurrentlySelectedItemIndex();
                            //initializeMediaPlayer(AudioSingleton.getInstance().getActivePlaylistAdapter().getSelectedAudio());
                            startMediaPlayer();
                            //playSelectedAudio(AudioSingleton.getInstance().getActivePlaylistAdapter().getSelectedItem(selectedItem));
                        }
                        else {
                            // AudioSingleton.getInstance().getActivePlaylistAdapter().setSelectedAudio(0);
                            // initializeMediaPlayer(AudioSingleton.getInstance().getActivePlaylistAdapter().getSelectedAudio());
                            //mediaPlayer.start();
                            //playSelectedAudio(AudioSingleton.getInstance().getActivePlaylistAdapter().getSelectedAudio());
                        }
                        // AudioSingleton.getInstance().getActivePlaylistAdapter().setSelectedAudio(selectedItem); // Set item at clicked position's isClicked to true
                        view.setBackgroundResource(R.drawable.ic_pause_24dp);
                    }
                } else { // mediaPlayer is not null
                    if (getItemCount() > 0) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();                                // Pause audio in mediaPlayer
                            view.setBackgroundResource(R.drawable.ic_play_24dp);
                        } else {
                            startMediaPlayer();                               // Play audio in mediaPlayer
                            view.setBackgroundResource(R.drawable.ic_pause_24dp);
                        }
                    } else {
                        mediaPlayer = null;
                    } // There aren't any songs queued, so we delete the mediaPlayer to enable the creation of a fresh one.
                }
                AudioSingleton.getInstance().getActivePlaylistAdapter().notifyDataSetChanged();
                //initializeSeekBar();
                break;
            }
            case "next_button": {
                playNextItem(); // Moves current selection to the next available item in the RecyclerView. Selects index 0 when called after reaching the end of the list.
                break;
            }
        }
    }

    // Selects the previous item in the list, if one is available, and plays the audio.
    // When at index 0 of audioList it will loop around and play the last item in the audioList.
    private void playPreviousItem() {
        //Controller controller = new Controller();
        //if (controller.getUser().getRecyclerViewPermission()) {  // Checks the global user state to see if the user has permission to alter the RecyclerView
        //TODO set permission up properly
        int currentlySelectedItemIndex = getCurrentlySelectedItemIndex();

        boolean isCurrentlyPlaying = false;
        MediaPlayer mediaPlayer = getMediaPlayer();
        if(mediaPlayer != null) {
            if(mediaPlayer.isPlaying()) {
                isCurrentlyPlaying = true;
            }
        }

        if (currentlySelectedItemIndex >= 0) {
            AudioController audioController = new AudioController();
            String time = audioController.milliSecondsToTimer(mediaPlayer.getCurrentPosition());
            int minutes = Integer.valueOf(time.substring(0, time.indexOf(':')));
            int seconds = Integer.valueOf(time.substring(time.indexOf(':') + 1)); // get all characters after ':'
            if (seconds < 2 && minutes == 0) {
                // go to previous song
                if (currentlySelectedItemIndex > 0) {
                    setSelectedAudio(currentlySelectedItemIndex - 1);
                    initializeMediaPlayer(getAudioAtIndex(currentlySelectedItemIndex - 1));
                }
            } else {
                setSelectedAudio(currentlySelectedItemIndex);
                initializeMediaPlayer(getAudioAtIndex(currentlySelectedItemIndex));
            }

            if(isCurrentlyPlaying) { // begin playing audio
                startMediaPlayer();
            }
            else { // reset seekbars and timestamp
                MainActivity mainActivity = new MainActivity();

                SeekBar bottomSheetSeekbar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_seekbar);
                SeekBar capstoneSeekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_seekbar);
                TextView timestamp = mainActivity.getInstance().findViewById(R.id.seekbar_position);
                bottomSheetSeekbar.setProgress(0);
                capstoneSeekBar.setProgress(0);
                timestamp.setText(mainActivity.getInstance().getResources().getString(R.string.timestamp));
            }
            togglePlayButtonState();
        }
        //}
    }

    // Selects the next available item in the RecyclerView if one is already selected to move from.
    // If the selected item is the last in the list, it selects the item at the top of the list instead (index 0).
    private void playNextItem() {
        //Controller controller = new Controller();
        //if (controller.getUser().getRecyclerViewPermission()) {  // Checks the global user state to see if the user has permission to alter the RecyclerView
        //TODO set permission up properly
        int currentlySelectedItemIndex = getCurrentlySelectedItemIndex();

        boolean isCurrentlyPlaying = false;
        MediaPlayer mediaPlayer = getMediaPlayer();
        if(mediaPlayer != null) {
            if(mediaPlayer.isPlaying()) {
                isCurrentlyPlaying = true;
            }
        }

        if (currentlySelectedItemIndex >= 0) {
            if (currentlySelectedItemIndex == getItemCount() - 1) { // the selected item is the last item in the list
                setSelectedAudio(0); // set the next audio to the first audio in the list
                initializeMediaPlayer(getAudioAtIndex(0));
            } else {
                setSelectedAudio(currentlySelectedItemIndex + 1);
                initializeMediaPlayer(getAudioAtIndex(currentlySelectedItemIndex + 1));
            }
            
            if(isCurrentlyPlaying) { // begin playing audio
                startMediaPlayer();
            }
            else { // reset seekbars and timestamp
                MainActivity mainActivity = new MainActivity();

                SeekBar bottomSheetSeekbar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_seekbar);
                SeekBar capstoneSeekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_seekbar);
                TextView timestamp = mainActivity.getInstance().findViewById(R.id.seekbar_position);
                bottomSheetSeekbar.setProgress(0);
                capstoneSeekBar.setProgress(0);
                timestamp.setText(mainActivity.getInstance().getResources().getString(R.string.timestamp));
            }
            togglePlayButtonState();
        }
        //}
    }

    public void togglePlayButtonState() {
        MainActivity mainActivity = new MainActivity();
        Button playButton = mainActivity.getInstance().findViewById(R.id.play_button);
        playButton.setBackgroundResource(R.drawable.ic_play_24dp);
        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                playButton.setBackgroundResource(R.drawable.ic_pause_24dp);
            }
        }
    }

    public void releaseSelectedAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            togglePlayButtonState();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Methods related to the active playlist but do not pertain to previous categories
     */

    private void showNotification(Audio audio) {
        MainActivity mainActivity = new MainActivity();
        Context context = mainActivity.getInstance();

        String title = audio.getTitle();
        String album = audio.getAlbum();
        String artist = audio.getArtist();
        Bitmap albumArt = audio.getAlbumArt();

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);


        }*/

        if (albumArt != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "id")
                    .setSmallIcon(R.drawable.audiopatch_logo_transparent)
                    .setLargeIcon(albumArt)
                    .setContentTitle(title)
                    .setContentText(artist + " - " + album)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "id")
                    .setSmallIcon(R.drawable.audiopatch_logo_transparent)
                    .setContentTitle(title)
                    .setContentText(artist + " - " + album)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, builder.build());
        }
    }
}