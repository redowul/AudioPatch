package com.colabella.connor.audiopatch.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.Audio.AudioSingleton;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.qhutch.bottomsheetlayout.BottomSheetLayout;

public class ActivePlaylistController {

    /**
     * Methods related to the Media Player
     */

    private static MediaPlayer mediaPlayer;

    MediaPlayer getMediaPlayer() {
        return mediaPlayer;
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
                    if (AudioSingleton.getInstance().getActivePlaylistAdapter().getItemCount() > 0) {
                        mediaPlayer = new MediaPlayer();
                        boolean isItemSelected = AudioSingleton.getInstance().getActivePlaylistAdapter().isItemSelected();
                        if(isItemSelected) {
                            int selectedItem = AudioSingleton.getInstance().getActivePlaylistAdapter().getCurrentlySelectedItemIndex();
                            playSelectedAudio(AudioSingleton.getInstance().getActivePlaylistAdapter().getSelectedItem(selectedItem));
                        }
                        else {
                            AudioSingleton.getInstance().getActivePlaylistAdapter().setSelectedAudio(0);
                            playSelectedAudio(AudioSingleton.getInstance().getActivePlaylistAdapter().getSelectedAudio());
                        }
                       // AudioSingleton.getInstance().getActivePlaylistAdapter().setSelectedAudio(selectedItem); // Set item at clicked position's isClicked to true
                        view.setBackgroundResource(R.drawable.ic_pause_24dp);
                    }
                } else { // mediaPlayer is not null
                    if (AudioSingleton.getInstance().getActivePlaylistAdapter().getItemCount() > 0) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();                                // Pause audio in mediaPlayer
                            view.setBackgroundResource(R.drawable.ic_play_24dp);
                        } else {
                            mediaPlayer.start();                                // Play audio in mediaPlayer
                            view.setBackgroundResource(R.drawable.ic_pause_24dp);
                        }
                    } else {
                        mediaPlayer = null;
                    } // There aren't any songs queued, so we delete the mediaPlayer to enable the creation of a fresh one.
                }
                AudioSingleton.getInstance().getActivePlaylistAdapter().notifyDataSetChanged();
                break;
            }
            case "next_button": {
                playNextItem(); // Moves current selection to the next available item in the RecyclerView. Selects index 0 when called after reaching the end of the list.
                break;
            }
        }
    }

    // This method is called whenever the selected item changes.
    // It checks the audioList for an item with a true selected boolean and then plays it.
    void playSelectedAudio(Audio selectedItem) {
        if (selectedItem != null) {                                  // avoids null pointer exception.
            MainActivity mainActivity = new MainActivity();
            Context context = mainActivity.getInstance();
            alterBottomSheet(selectedItem); // applies song data to bottom sheet

            String audioToPlayString = selectedItem.getData();  // Convert Audio to String
            Uri uri = Uri.parse(audioToPlayString);             // Convert to Uri by parsing the String

            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            if (context != null && uri != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = MediaPlayer.create(context, uri);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    ActivePlaylistAdapter activePlaylistAdapter = new ActivePlaylistAdapter();
                    if (activePlaylistAdapter.getCurrentlySelectedItemIndex() == activePlaylistAdapter.getItemCount() - 1) { // if song played was last in the list
                        activePlaylistAdapter.setSelectedAudio(0);
                        int currentlySelectedItemIndex = activePlaylistAdapter.getCurrentlySelectedItemIndex();
                        Audio currentlySelectedItem = AudioSingleton.getInstance().getActivePlaylistAdapter().getSelectedItem(currentlySelectedItemIndex);
                        playSelectedAudio(currentlySelectedItem);
                    } else {
                        activePlaylistAdapter.setSelectedAudio(activePlaylistAdapter.getCurrentlySelectedItemIndex() + 1);
                        int currentlySelectedItemIndex = activePlaylistAdapter.getCurrentlySelectedItemIndex();
                        Audio currentlySelectedItem = AudioSingleton.getInstance().getActivePlaylistAdapter().getSelectedItem(currentlySelectedItemIndex);
                        playSelectedAudio(currentlySelectedItem);
                    }
                });
                showNotification(selectedItem);
            }
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
            } else {
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

    void releaseSelectedAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            togglePlayButtonState();
            mediaPlayer.release();
            mediaPlayer = null;
        }
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

    /**
     * Methods related to the Bottom Sheet
     */

    @SuppressLint("NewApi")
    Bitmap blur(Context context, Bitmap image) {
        float BITMAP_SCALE = 0.4f;
        float BLUR_RADIUS = 7.5f;

        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        @SuppressLint({"NewApi", "LocalSuppress"}) ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    // Changes the bottom sheet background and displays information about currently selected item, if one exists.
    public void alterBottomSheet(Audio selectedItem) {
        if (selectedItem != null) {                                  // audioToPlay can return null if there's nothing in the audioList, so this avoids a null pointer exception.
            MainActivity mainActivity = new MainActivity();
            ImageView bottomSheetAlbumCover = mainActivity.getInstance().findViewById(R.id.bottom_sheet_album_cover);
            ImageView bottomSheetCapstoneAlbumCover = mainActivity.getInstance().findViewById(R.id.bottom_sheet_current_album_cover_small);

            if (selectedItem.getAlbumArt() != null) {
                Bitmap blurredAlbumCover = blur(mainActivity.getInstance(), selectedItem.getAlbumArt());
                bottomSheetAlbumCover.setImageBitmap(blurredAlbumCover); // Set background of the bottom sheet
                bottomSheetCapstoneAlbumCover.setImageBitmap(selectedItem.getAlbumArt()); // Set background of the bottom sheet capstone image
                bottomSheetCapstoneAlbumCover.getDrawable().setAlpha(0);

                //BottomSheetLayout layout = mainActivity.getInstance().findViewById(R.id.bottom_sheet_layout);
               // AppBarLayout bottomSheetLayoutCapstone = mainActivity.getInstance().findViewById(R.id.bottom_sheet_layout_capstone);
                /*double maxY = layout.getBottom();
                double currentY = layout.getY();
                double adjustedMaxY = bottomSheetLayoutCapstone.getHeight();
                if (adjustedMaxY == maxY - currentY) {
                    bottomSheetCapstoneAlbumCover.getDrawable().setAlpha(255);
                }*/

            } else {
                Bitmap blurredAlbumCover = BitmapFactory.decodeResource(mainActivity.getInstance().getResources(), R.drawable.audiopatchlogosquareblurrable); // getting the resource, it isn't blurred yet
                bottomSheetCapstoneAlbumCover.setImageBitmap(blurredAlbumCover);   // Set background of the bottom sheet capstone image; this one is not blurred

                blurredAlbumCover = blur(mainActivity.getInstance(), blurredAlbumCover); // blur the image
                bottomSheetAlbumCover.setImageBitmap(blurredAlbumCover);   // Set background of the bottom sheet

                BottomSheetLayout layout = mainActivity.getInstance().findViewById(R.id.bottom_sheet_layout);
                if (layout.isExpended()) {
                    bottomSheetCapstoneAlbumCover.getDrawable().setAlpha(0);
                }
            }

            TextView bottomSheetCapstoneTitle = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_title);
            TextView bottomSheetCapstoneArtist = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_artist);

            TextView bottomSheetTitle = mainActivity.getInstance().findViewById(R.id.bottom_sheet_title);
            TextView bottomSheetArtist = mainActivity.getInstance().findViewById(R.id.bottom_sheet_artist);
            TextView bottomSheetSubmitter = mainActivity.getInstance().findViewById(R.id.bottom_sheet_submitter);

            bottomSheetCapstoneTitle.setText(selectedItem.getTitle());
            bottomSheetCapstoneArtist.setText(selectedItem.getArtist());

            bottomSheetTitle.setText(selectedItem.getTitle());
            bottomSheetArtist.setText(selectedItem.getArtist());
            String submittedBy = "Submitted by " + mainActivity.getInstance().getResources().getString(R.string.submitter, selectedItem.getSubmitter());
            bottomSheetSubmitter.setText(submittedBy);

            Button backButton = mainActivity.getInstance().findViewById(R.id.back_button);
            Button playButton = mainActivity.getInstance().findViewById(R.id.play_button);
            Button nextButton = mainActivity.getInstance().findViewById(R.id.next_button);
            backButton.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    // resets the background images within the bottom sheet when the active playlist size is reduced to 0
    public void resetBottomSheet() {
        MainActivity mainActivity = new MainActivity();
        BottomSheetLayout layout = mainActivity.getInstance().findViewById(R.id.bottom_sheet_layout);
        ImageView bottomSheetCapstoneAlbumCover = mainActivity.getInstance().findViewById(R.id.bottom_sheet_current_album_cover_small);
        bottomSheetCapstoneAlbumCover.setImageBitmap(null);
        if(layout.isExpended()) {
            layout.collapse();
        }
        else {
            Bitmap blurredAlbumCover = BitmapFactory.decodeResource(mainActivity.getInstance().getResources(), R.drawable.audiopatchlogosquareblurrable); // getting the resource, it isn't blurred yet

            ActivePlaylistController activePlaylistController = new ActivePlaylistController();
            blurredAlbumCover = activePlaylistController.blur(mainActivity.getInstance(), blurredAlbumCover); // blur the image
            ImageView bottomSheetAlbumCover = mainActivity.getInstance().findViewById(R.id.bottom_sheet_album_cover);
            bottomSheetAlbumCover.setImageBitmap(blurredAlbumCover);   // Set background of the bottom sheet
        }

        TextView bottomSheetCapstoneTitle = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_title);
        TextView bottomSheetCapstoneArtist = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_artist);

        TextView bottomSheetTitle = mainActivity.getInstance().findViewById(R.id.bottom_sheet_title);
        TextView bottomSheetArtist = mainActivity.getInstance().findViewById(R.id.bottom_sheet_artist);
        TextView bottomSheetSubmitter = mainActivity.getInstance().findViewById(R.id.bottom_sheet_submitter);

        bottomSheetCapstoneTitle.setText("");
        bottomSheetCapstoneArtist.setText("");
        bottomSheetTitle.setText("");
        bottomSheetArtist.setText("");
        bottomSheetSubmitter.setText("");

        Button backButton = mainActivity.getInstance().findViewById(R.id.back_button);
        Button playButton = mainActivity.getInstance().findViewById(R.id.play_button);
        Button nextButton = mainActivity.getInstance().findViewById(R.id.next_button);
        backButton.setVisibility(View.GONE);
        playButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
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
                    .setSmallIcon(R.drawable.audiopatchlogotransparent)
                    .setLargeIcon(albumArt)
                    .setContentTitle(title)
                    .setContentText(artist + " - " + album)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "id")
                    .setSmallIcon(R.drawable.audiopatchlogotransparent)
                    .setContentTitle(title)
                    .setContentText(artist + " - " + album)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, builder.build());
        }
    }
}