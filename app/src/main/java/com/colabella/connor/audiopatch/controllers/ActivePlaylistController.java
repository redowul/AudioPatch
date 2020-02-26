package com.colabella.connor.audiopatch.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.recyclerview.ActivePlaylistAdapter;

import java.util.Random;

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
                    boolean repeatPlaylist = false;
                    boolean repeatSong = false;
                    boolean shufflePlaylist = false;
                    Button repeatButton = mainActivity.getInstance().findViewById(R.id.repeat_button);
                    Drawable.ConstantState repeatButtonCurrentState = repeatButton.getBackground().getConstantState();

                    Button shuffleButton = mainActivity.getInstance().findViewById(R.id.shuffle_button);
                    Drawable.ConstantState shuffleButtonCurrentState = shuffleButton.getBackground().getConstantState();

                    if (repeatButtonCurrentState != null && shuffleButtonCurrentState != null) {
                        Drawable.ConstantState repeatSelected = mainActivity.getInstance().getResources().getDrawable(R.drawable.repeat_selected_24dp).getConstantState();
                        Drawable.ConstantState repeatOneSelected = mainActivity.getInstance().getResources().getDrawable(R.drawable.repeat_one_selected_24dp).getConstantState();
                        Drawable.ConstantState shuffleSelected = mainActivity.getInstance().getResources().getDrawable(R.drawable.shuffle_selected_24dp).getConstantState();

                        if(repeatButtonCurrentState.equals(repeatSelected)) {
                            repeatPlaylist = true;
                        }
                        else if (repeatButtonCurrentState.equals(repeatOneSelected)) {
                            repeatSong = true;
                        }
                        else if(shuffleButtonCurrentState.equals(shuffleSelected)) {
                            shufflePlaylist = true;
                        }
                    }

                    Audio currentlySelectedItem;
                    if(!repeatSong) { // Default state, which is skipped if an alternative trigger is tripped above (repeating)
                        //TODO add a boolean to the audio objects so we can mark them off when searching for a random song to play. (So we can play every song out of order without repeats)
                        if(shufflePlaylist) { // play a random song in the playlist
                            int playlistSize = getItemCount();

                            Random random = new Random();
                            int randomNumber = random.nextInt(playlistSize); // generate random number ranging from 0 to the size of the playlist (exclusive) e.g. input of 3 generates range of 0 to 2

                            setSelectedAudio(randomNumber);
                            currentlySelectedItem = getSelectedAudio();
                            initializeMediaPlayer(currentlySelectedItem);
                            startMediaPlayer();
                            togglePlayButtonState();
                        }
                        else {
                            int currentlySelectedItemIndex = getCurrentlySelectedItemIndex();
                            if (currentlySelectedItemIndex == getItemCount() - 1) { // if song played was last in the list
                                SeekBar seekbar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_seekbar);
                                seekbar.setProgress(0);

                                if (repeatPlaylist) { // repeatPlaylist is enabled, so we can loop the playlist
                                    setSelectedAudio(0);
                                    currentlySelectedItem = getSelectedAudio();
                                    initializeMediaPlayer(currentlySelectedItem);
                                    startMediaPlayer();
                                }
                                else { // notify the playlist that the song is finished
                                    SingletonController.getInstance().getActivePlaylistAdapter().notifyDataSetChanged();
                                }
                                togglePlayButtonState();
                            } else { // item isn't the last item, so we play it
                                setSelectedAudio(getCurrentlySelectedItemIndex() + 1);
                                currentlySelectedItem = getSelectedAudio();
                                initializeMediaPlayer(currentlySelectedItem);
                                startMediaPlayer();
                            }
                        }
                    }
                    else { // repeat the current track
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
            }
            break;
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
                SingletonController.getInstance().getActivePlaylistAdapter().notifyDataSetChanged();
            }
            break;
            case "next_button": {
                playNextItem(); // Moves current selection to the next available item in the RecyclerView. Selects index 0 when called after reaching the end of the list.
            }
            break;
            case "repeat_button": {
                repeatButtonPressed();
            }
            break;
            case "shuffle_button": {
                shuffleButtonPressed();
            }
            break;
        }
    }

    private void repeatButtonPressed() {
        MainActivity mainActivity = new MainActivity();
        Button repeatButton = mainActivity.getInstance().findViewById(R.id.repeat_button);
        Drawable.ConstantState repeatUnselected = mainActivity.getInstance().getResources().getDrawable(R.drawable.ic_repeat_24dp).getConstantState();
        Drawable.ConstantState repeatSelected = mainActivity.getInstance().getResources().getDrawable(R.drawable.repeat_selected_24dp).getConstantState();
        // Drawable.ConstantState repeatOneSelected = mainActivity.getInstance().getResources().getDrawable(R.drawable.repeat_one_selected_24dp).getConstantState();

        Drawable.ConstantState currentState = repeatButton.getBackground().getConstantState();

        if (currentState != null) {
            if (currentState.equals(repeatUnselected)) {
                repeatButton.setBackgroundResource(R.drawable.repeat_selected_24dp); // swap to repeat playlist icon
            } else if (currentState.equals(repeatSelected)) {
                repeatButton.setBackgroundResource(R.drawable.repeat_one_selected_24dp); // swap to repeat song icon
            } else {
                repeatButton.setBackgroundResource(R.drawable.ic_repeat_24dp); // swap to repeat unselected icon
            }
        }
    }

    private void shuffleButtonPressed() {
        MainActivity mainActivity = new MainActivity();
        Button shuffleButton = mainActivity.getInstance().findViewById(R.id.shuffle_button);

        Drawable.ConstantState shuffleUnselected = mainActivity.getInstance().getResources().getDrawable(R.drawable.shuffle_24dp).getConstantState();
        Drawable.ConstantState currentState = shuffleButton.getBackground().getConstantState();

        if (currentState != null) {
            if (currentState.equals(shuffleUnselected)) {
                shuffleButton.setBackgroundResource(R.drawable.shuffle_selected_24dp); // swap to shuffle selected
            } else {
                shuffleButton.setBackgroundResource(R.drawable.shuffle_24dp); // swap to shuffle unselected
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
            if(mediaPlayer != null) {
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

                if (isCurrentlyPlaying) { // begin playing audio
                    startMediaPlayer();
                } else { // reset seekbars and timestamp
                    MainActivity mainActivity = new MainActivity();

                    SeekBar bottomSheetSeekbar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_seekbar);
                    SeekBar capstoneSeekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_seekbar);
                    TextView timestamp = mainActivity.getInstance().findViewById(R.id.seekbar_position);
                    bottomSheetSeekbar.setProgress(0);
                    capstoneSeekBar.setProgress(0);
                    timestamp.setText(mainActivity.getInstance().getResources().getString(R.string.timestamp));
                }
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