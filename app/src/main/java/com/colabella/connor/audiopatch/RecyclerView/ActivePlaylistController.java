package com.colabella.connor.audiopatch.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Button;

import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.Audio.AudioSingleton;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;

public class ActivePlaylistController {

    private static MediaPlayer mediaPlayer;

    // Determines which button on the bottom toolbar was pressed
    public void determineButtonSelected(String buttonIdString, View view) {
        switch (buttonIdString) {
            case "remove":
                //  removeItem();                           // TODO Removes an item from the RecyclerView (Remove this function later. Exists only for bug-testing purposes)
                break;
            case "back_button":
                playPreviousItem();                   // Moves current selection to the previous available item in the RecyclerView. Selects the last item in the list if pressed at index 0.
                break;
            case "play_button":
                if (mediaPlayer == null) { // mediaPlayer is null
                    if (AudioSingleton.getInstance().getActivePlaylistAdapter().getItemCount() > 0) {
                        mediaPlayer = new MediaPlayer();
                        playSelectedAudio(AudioSingleton.getInstance().getActivePlaylistAdapter().getSelectedItem(0));
                        AudioSingleton.getInstance().getActivePlaylistAdapter().setSelectedAudio(0); // Set item at clicked position's isClicked to true
                        ((Button) view).setBackgroundResource(R.drawable.ic_pause_24dp);
                    }
                } else { // mediaPlayer is not null
                    if (AudioSingleton.getInstance().getActivePlaylistAdapter().getItemCount() > 0) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();                                // Pause audio in mediaPlayer
                            ((Button) view).setBackgroundResource(R.drawable.ic_play_24dp);
                        } else {
                            mediaPlayer.start();                                // Play audio in mediaPlayer
                            ((Button) view).setBackgroundResource(R.drawable.ic_pause_24dp);
                        }
                    } else {
                        mediaPlayer = null;
                    } // There aren't any songs queued, so we delete the mediaPlayer to enable the creation of a fresh one.
                }
                // TODO Determine which methods should be called in the logic here.
                // - If mediaPlayer is null, playSelectedItem() should be invoked
                // - If mediaPlayer is not null (and audio is playing), the audio should be paused and the play button should be changed to a pause button.
                // - Meaning if the mediaPlayer is not null and no audio is playing, we should just un-pause the audio.
                // - The mediaPlayer will thus handle itself when a piece of audio ends, independent of this button.
                break;
            case "next_button":
                playNextItem();                       // Moves current selection to the next available item in the RecyclerView. Selects index 0 when called after reaching the end of the list.
                break;
            case "add_audio_button":                    // TODO Adds a new item to the RecyclerView (Remove this function later. Exists only for bug-testing purposes)
                addItem();
                break;
            case "toggle_recyclerview_permission":      // TODO Toggles recyclerview permissions (Remove this function later. Exists only for bug-testing purposes)
                //   toggleRecyclerViewItemClickability();
                break;
        }
    }

  /*  private void removeItem() {
        Controller controller = new Controller();
        AudioController audioController = new AudioController();

        RecyclerViewAdapter recyclerViewAdapter = audioController.getRecyclerViewAdapter();
        if (controller.getUser().getRecyclerViewPermission()) {
            List<Audio> audioList = audioController.getAudioList();
            if (audioList.size() > 0) {
                audioList.remove(audioList.size() - 1);
                audioController.setAudioList(audioList);
                recyclerViewAdapter.notifyDataSetChanged();
                audioController.setRecyclerViewAdapter(recyclerViewAdapter);
            }
        }
    }
*/

    // This method is called whenever the selected item changes.
    // It checks the audioList for an item with a true selected boolean and then plays it.
    void playSelectedAudio(Audio selectedItem) {
        if (selectedItem != null) {                                  // audioToPlay can return null if there's nothing in the audioList, so this avoids a null pointer exception.
            MainActivity mainActivity = new MainActivity();
            Context context = mainActivity.getInstance();

            String audioToPlayStringified = selectedItem.getData();  // Convert Audio to String
            Uri uri = Uri.parse(audioToPlayStringified);             // Convert to Uri by parsing the String

            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                if (context != null && uri != null) {
                    mediaPlayer = MediaPlayer.create(context, uri);
                    mediaPlayer.start();
                    showNotification(selectedItem);
                }
            } else {  // MediaPlayer is not null
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(context, uri);
                try {
                    mediaPlayer.start();
                    showNotification(selectedItem);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    private void showNotification(Audio audio){
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

        if(albumArt != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "id")
                    .setSmallIcon(R.drawable.audiopatchlogotransparent)
                    .setLargeIcon(albumArt)
                    .setContentTitle(title)
                    .setContentText(artist + " - " + album)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, builder.build());
        }
        else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "id")
                    .setSmallIcon(R.drawable.audiopatchlogotransparent)
                    .setContentTitle(title)
                    .setContentText(artist + " - " + album)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, builder.build());
        }
    }

    // Selects the previous item in the list, if one is available, and plays the audio.
    // When at index 0 of audioList it will loop around and play the last item in the audioList.
    private void playPreviousItem() {
        //Controller controller = new Controller();
        //if (controller.getUser().getRecyclerViewPermission()) {  // Checks the global user state to see if the user has permission to alter the RecyclerView
        //TODO set permission up properly
        ActivePlaylistAdapter activePlaylistAdapter = new ActivePlaylistAdapter();
        int currentlySelectedItemIndex = activePlaylistAdapter.getCurrentlySelectedItemIndex();

        if (currentlySelectedItemIndex >= 0) {
            AudioController audioController = new AudioController();
            String time = audioController.milliSecondsToTimer(mediaPlayer.getCurrentPosition());
            int minutes = Integer.valueOf(time.substring(0, time.indexOf(':')));
            int seconds = Integer.valueOf(time.substring(time.indexOf(':') + 1)); // get all characters after ':'
            if (seconds < 2 && minutes == 0) {
                // go to previous song
                if (currentlySelectedItemIndex > 0) {
                    activePlaylistAdapter.setSelectedAudio(currentlySelectedItemIndex - 1);
                    playSelectedAudio(activePlaylistAdapter.getAudioAtIndex(currentlySelectedItemIndex - 1));
                    togglePlayButtonState();
                }
            }
            else {
                activePlaylistAdapter.setSelectedAudio(currentlySelectedItemIndex);
                playSelectedAudio(activePlaylistAdapter.getAudioAtIndex(currentlySelectedItemIndex));
                togglePlayButtonState();
            }
        }
        //}
    }

    // Selects the next available item in the RecyclerView if one is already selected to move from.
    // If the selected item is the last in the list, it selects the item at the top of the list instead (index 0).
    private void playNextItem() {
        //Controller controller = new Controller();
        //if (controller.getUser().getRecyclerViewPermission()) {  // Checks the global user state to see if the user has permission to alter the RecyclerView
        //TODO set permission up properly
        ActivePlaylistAdapter activePlaylistAdapter = new ActivePlaylistAdapter();
        int currentlySelectedItemIndex = activePlaylistAdapter.getCurrentlySelectedItemIndex();

        if (currentlySelectedItemIndex >= 0) {
            if (currentlySelectedItemIndex == activePlaylistAdapter.getItemCount() - 1) { // the selected item is the last item in the list
                activePlaylistAdapter.setSelectedAudio(0); // set the next audio to the first audio in the list
                playSelectedAudio(activePlaylistAdapter.getAudioAtIndex(0));
            } else {
                activePlaylistAdapter.setSelectedAudio(currentlySelectedItemIndex + 1);
                playSelectedAudio(activePlaylistAdapter.getAudioAtIndex(currentlySelectedItemIndex + 1));
            }
            togglePlayButtonState();
        }
        //}
    }

    /*
    private void toggleRecyclerViewItemClickability() {
        Controller controller = new Controller();
        AudioController audioController = new AudioController();

        RecyclerViewAdapter recyclerViewAdapter = audioController.getRecyclerViewAdapter();
        User user = controller.getUser();             // Get an instance of the global user state
        user.toggleRecyclerViewPermission();          // Toggle the state of our user's RecyclerView permission
        controller.setUser(user);                     // Set the global user state equal to that of our instance
        recyclerViewAdapter.notifyDataSetChanged();
    }
    */

    void togglePlayButtonState() {
        MainActivity mainActivity = new MainActivity();
        Button playButton = mainActivity.getInstance().findViewById(R.id.play_button);
        playButton.setBackgroundResource(R.drawable.ic_play_24dp);
        if (mediaPlayer.isPlaying()) {
            playButton.setBackgroundResource(R.drawable.ic_pause_24dp);
        }
    }

    private void addItem() {
        //if (controller.getUser().getRecyclerViewPermission()) {
        Audio item = new Audio("Item", false);
        AudioSingleton.getInstance().getActivePlaylistAdapter().addItem(item);
        AudioSingleton.getInstance().getActivePlaylistAdapter().notifyDataSetChanged();
        //}
    }

    void releaseSelectedAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            togglePlayButtonState();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
/*
    public void clearAudioList(){
        Controller controller = new Controller();
        AudioController audioController = new AudioController();
        RecyclerViewAdapter recyclerViewAdapter = audioController.getRecyclerViewAdapter();

        if (controller.getUser().getRecyclerViewPermission()) {
            List<Audio> audioList = audioController.getAudioList();
            audioList.clear();
            audioController.setAudioList(audioList);
            recyclerViewAdapter.notifyDataSetChanged();
            audioController.releaseSelectedAudio();                           // Releases selected audio from the MediaPlayer
        }
    }
}*/


}
