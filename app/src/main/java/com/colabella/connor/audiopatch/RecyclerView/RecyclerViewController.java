package com.colabella.connor.audiopatch.RecyclerView;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.Controller;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.NearbyConnections.User;
import com.colabella.connor.audiopatch.R;

import java.util.List;

public class RecyclerViewController {
    // Determines which button on the bottom toolbar was pressed
    public void determineButtonSelected(String buttonIdString, View view) {
        switch (buttonIdString) {
            case "remove":
                removeItem();                           // TODO Removes an item from the RecyclerView (Remove this function later. Exists only for bug-testing purposes)
                break;
            case "back_button":
                selectPreviousItem();                   // Moves current selection to the previous available item in the RecyclerView. Selects the last item in the list if pressed at index 0.
                break;
            case "play_button":
                AudioController audioController = new AudioController();
                MediaPlayer mediaPlayer = audioController.getMediaPlayer();
                if(mediaPlayer == null) {
                    List<Audio> audioList = audioController.getAudioList();
                    playSelectedItem();               // Plays the audio of the selected item. If no item is selected, it will play the first item in the list.
                    if(audioList.size() > 0) { ((Button) view).setBackgroundResource(R.drawable.ic_pause_24dp); }
                }
                else{
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();                                // Pause audio in mediaPlayer
                        ((Button) view).setBackgroundResource(R.drawable.ic_play_24dp);
                    }
                    else {
                        mediaPlayer.start();                                // Play audio in mediaPlayer
                        ((Button) view).setBackgroundResource(R.drawable.ic_pause_24dp);
                    }
                    audioController.setMediaPlayer(mediaPlayer);            // Update the global mediaPlayer state
                }
                // TODO Determine which methods should be called in the logic here.
                    // - If mediaPlayer is null, playSelectedItem() should be invoked
                    // - If mediaPlayer is not null (and audio is playing), the audio should be paused and the play button should be changed to a pause button.
                    // - Meaning if the mediaPlayer is not null and no audio is playing, we should just un-pause the audio.
                    // - The mediaPlayer will thus handle itself when a piece of audio ends, independent of this button.


                break;
            case "next_button":
                selectNextItem();                       // Moves current selection to the next available item in the RecyclerView. Selects index 0 when called after reaching the end of the list.
                break;
            case "add_audio_button":                    // TODO Adds a new item to the RecyclerView (Remove this function later. Exists only for bug-testing purposes)
                addItem();
                break;
            case "toggle_recyclerview_permission":      // TODO Toggles recyclerview permissions (Remove this function later. Exists only for bug-testing purposes)
                toggleRecyclerViewItemClickability();
                break;
        }
    }

    private void removeItem() {
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

    // This method is called whenever the selected item changes.
    // It checks the audioList for an item with a true selected boolean and then plays it.
    void playSelectedItem() {
        MainActivity mainActivity = new MainActivity();
        AudioController audioController = new AudioController();
        RecyclerViewAdapter recyclerViewAdapter = audioController.getRecyclerViewAdapter();
        Context context = mainActivity. getStaticApplicationContext();

        Audio audioToPlay = audioController.getSelectedAudio();             // Retrieving the presently selected audio, if there is one.
        if (audioToPlay == null) {
            if(audioController.getAudioList().size() > 0){                  // Check to see if there's any data in the audioList.
                audioController.setSelectedAudio(0);                        // If there is, set the first item in the list to selected.
                audioToPlay = audioController.getSelectedAudio();           // If getSelectedAudio() returns null, we want to play the first item in the list (index 0).
            }
        }
        if(audioToPlay != null) {                                           // audioToPlay can return null if there's nothing in the audioList, so this avoids a null pointer exception.
            recyclerViewAdapter.notifyDataSetChanged();
            audioController.setRecyclerViewAdapter(recyclerViewAdapter);

            String audioToPlayStringified = audioToPlay.getData();          // Convert Audio to String
            Uri audioToPlayUri = Uri.parse(audioToPlayStringified);         // Convert to Uri by parsing the String

            audioController.playSelectedAudio(context, audioToPlayUri);     // Sends the data to the Audio Controller
        }
    }

    // Selects the previous item in the list, if one is available, and plays the audio.
    // When at index 0 of audioList it will loop around and play the last item in the audioList.
    private void selectPreviousItem() {
        Controller controller = new Controller();
        AudioController audioController = new AudioController();
        RecyclerViewAdapter recyclerViewAdapter = audioController.getRecyclerViewAdapter();

        if (controller.getUser().getRecyclerViewPermission()) {             // Checks the global user state to see if the user has permission to alter the RecyclerView
            List<Audio> audioList = audioController.getAudioList();
            for (int index = 0; index < audioList.size(); index++) {        // Iterates over the audio list to find the currently selected item
                if (audioList.get(index).getSelected()) {                   // Proceed if an item's selected boolean is true
                    if (index - 1 >= 0) {                                   // Proceed if the next index is less than the list size (an item in a list of size 1 has an index of 0)
                        audioController.setSelectedAudio(index - 1);        // Method to sets the next index as selected.
                        recyclerViewAdapter.notifyDataSetChanged();
                        playSelectedItem();                                 // Play the newly selected item
                        break;
                    } else {                                                // We've reached the end of the list, so select the first item (index 0) instead.
                        audioController.setSelectedAudio(audioList.size() - 1);
                        recyclerViewAdapter.notifyDataSetChanged();
                        playSelectedItem();                                 // Play the newly selected item
                        break;
                    }
                }
            }
        }
    }

    // Selects the next available item in the RecyclerView if one is already selected to move from.
    // If the selected item is the last in the list, it selects the item at the top of the list instead (index 0).
    private void selectNextItem() {
        Controller controller = new Controller();
        AudioController audioController = new AudioController();
        RecyclerViewAdapter recyclerViewAdapter = audioController.getRecyclerViewAdapter();

        if (controller.getUser().getRecyclerViewPermission()) {             // Checks the global user state to see if the user has permission to alter the RecyclerView
            List<Audio> audioList = audioController.getAudioList();
            for (int index = 0; index < audioList.size(); index++) {        // Iterates over the audio list to find the currently selected item
                if (audioList.get(index).getSelected()) {                   // Proceed if an item's selected boolean is true
                    if (index + 1 < audioList.size()) {                     // Proceed if the next index is less than the list size (an item in a list of size 1 has an index of 0)
                        audioController.setSelectedAudio(index + 1);        // Method to sets the next index as selected.
                        recyclerViewAdapter.notifyDataSetChanged();
                        playSelectedItem();                                 // Play the newly selected item
                        break;
                    } else {                                                // We've reached the end of the list, so select the first item (index 0) instead.
                        audioController.setSelectedAudio(0);
                        recyclerViewAdapter.notifyDataSetChanged();
                        playSelectedItem();                                 // Play the newly selected item
                        break;
                    }
                }
            }
        }
    }

    private void toggleRecyclerViewItemClickability() {
        Controller controller = new Controller();
        AudioController audioController = new AudioController();

        RecyclerViewAdapter recyclerViewAdapter = audioController.getRecyclerViewAdapter();
        User user = controller.getUser();             // Get an instance of the global user state
        user.toggleRecyclerViewPermission();          // Toggle the state of our user's RecyclerView permission
        controller.setUser(user);                     // Set the global user state equal to that of our instance
        recyclerViewAdapter.notifyDataSetChanged();

        //TODO bugfixing. Remove this
        for (int i = 0; i < audioController.getAudioList().size(); i++) {
            System.out.println(i + " : " + audioController.getAudioList().get(i).getTitle());
        }
    }

    public void togglePlayButtonState(){ //TODO re-write when less tired (Needed for any action that isn't a button to toggle the state of the PlayButton)
        MainActivity mainActivity = new MainActivity();
        AudioController audioController = new AudioController();
        Button playButton = mainActivity.getPlayButton();
        MediaPlayer mediaPlayer = audioController.getMediaPlayer();

        playButton.setBackgroundResource(R.drawable.ic_pause_24dp);
        if(mediaPlayer == null){
            playButton.setBackgroundResource(R.drawable.ic_play_24dp);
        }
    }

    private void addItem() {
        AudioController audioController = new AudioController();
        RecyclerViewAdapter recyclerViewAdapter = audioController.getRecyclerViewAdapter();

        //if (controller.getUser().getRecyclerViewPermission()) {
        Audio item = new Audio("Item", false);
        List<Audio> audioList = audioController.getAudioList();
        audioList.add(item);
        audioController.setAudioList(audioList);
        recyclerViewAdapter.notifyDataSetChanged();
        audioController.setRecyclerViewAdapter(recyclerViewAdapter);
        //}
    }

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
}
