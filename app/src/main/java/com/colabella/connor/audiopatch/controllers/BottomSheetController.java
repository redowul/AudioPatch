package com.colabella.connor.audiopatch.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.recyclerview.ActivePlaylistAdapter;
import com.qhutch.bottomsheetlayout.BottomSheetLayout;

public class BottomSheetController extends ActivePlaylistController {

    /**
     * Methods related to the Bottom Sheet
     */

    @SuppressLint("NewApi")
    private Bitmap blur(Context context, Bitmap image) {
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

            BottomSheetLayout layout = mainActivity.getInstance().findViewById(R.id.bottom_sheet_layout);
            RelativeLayout bottomSheetLayoutCapstone = mainActivity.getInstance().findViewById(R.id.bottom_sheet_layout_capstone); // needed for calculating bottom Y value of the capstone

            double capstoneMaxY = bottomSheetLayoutCapstone.getBottom(); // bottom Y value of the capstone
            double minY = layout.getTop(); // upper Y value of the layout

            // bottom Y value of the layout minus the bottom Y value of the capstone
            // (accounts for the difference of ~30% caused by the capstone blocking the bottom sheet from resting flush on the bottom of the screen)
            double maxY = layout.getBottom() - capstoneMaxY;
            double currentY = layout.getY(); // current Y value of the top of the sheet

            // used for calculating our percentage value below; MinY, maxY, and currentY are values relative to the entire screen, but we only want to know the Y value relative to the bottom sheet
            // therefore we subtract the minimum Y value from the maximum Y value to get the difference, which is the total size of the bottom sheet
            double bottomSheetSize = maxY - minY;
            double currentAdjustedY = currentY - minY; // Y position relative to the top and bottom Y values of the bottom sheet, not the entire screen
            double percentage = (currentAdjustedY / bottomSheetSize) * 100; // used for calculating the percentage needed for rotation and transparency
            int alpha = (int) ((percentage / 100) * 255); // calculates level of transparency to be applied

            if (selectedItem.getAlbumArt() != null) {
                Bitmap blurredAlbumCover = blur(mainActivity.getInstance(), selectedItem.getAlbumArt());
                bottomSheetAlbumCover.setImageBitmap(blurredAlbumCover); // Set background of the bottom sheet
                bottomSheetCapstoneAlbumCover.setImageBitmap(selectedItem.getAlbumArt()); // Set background of the bottom sheet capstone image

            } else {
                // getting the resource, it isn't blurred yet
                Bitmap blurredAlbumCover = BitmapFactory.decodeResource(mainActivity.getInstance().getResources(), R.drawable.audiopatch_logo_square_blurrable);
                bottomSheetCapstoneAlbumCover.setImageBitmap(blurredAlbumCover);   // Set background of the bottom sheet capstone image; this one is not blurred

                blurredAlbumCover = blur(mainActivity.getInstance(), blurredAlbumCover); // blur the image
                bottomSheetAlbumCover.setImageBitmap(blurredAlbumCover);   // Set background of the bottom sheet
            }
            bottomSheetCapstoneAlbumCover.getDrawable().setAlpha(alpha); // set the alpha of the bitmap overlain on top of the image view

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
            Button repeatButton = mainActivity.getInstance().findViewById(R.id.repeat_button);
            Button shuffleButton = mainActivity.getInstance().findViewById(R.id.shuffle_button);
            SeekBar bottomSheetSeekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_seekbar);
            TextView seekBarPosition = mainActivity.getInstance().findViewById(R.id.seekbar_position); // textView of the current position (e.g. 1:23) of the current audio file
            TextView audioLength = mainActivity.getInstance().findViewById(R.id.audio_length); // textView of the total duration of the current audio file
            backButton.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            repeatButton.setVisibility(View.VISIBLE);
            shuffleButton.setVisibility(View.VISIBLE);
            bottomSheetSeekBar.setVisibility(View.VISIBLE);
            seekBarPosition.setVisibility(View.VISIBLE);
            audioLength.setVisibility(View.VISIBLE);

            SeekBar capstoneSeekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_seekbar);
            capstoneSeekBar.setVisibility(View.VISIBLE);
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
            // getting the resource, it isn't blurred yet
            Bitmap blurredAlbumCover = BitmapFactory.decodeResource(mainActivity.getInstance().getResources(), R.drawable.audiopatch_logo_square_blurrable);

            blurredAlbumCover = blur(mainActivity.getInstance(), blurredAlbumCover); // blur the image
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
        Button repeatButton = mainActivity.getInstance().findViewById(R.id.repeat_button);
        Button shuffleButton = mainActivity.getInstance().findViewById(R.id.shuffle_button);
        SeekBar bottomSheetSeekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_seekbar);
        backButton.setVisibility(View.GONE);
        playButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);

        repeatButton.setBackgroundResource(R.drawable.ic_repeat_24dp); // swap to repeat unselected icon
        shuffleButton.setBackgroundResource(R.drawable.shuffle_24dp); // swap to shuffle unselected
        repeatButton.setVisibility(View.GONE);
        shuffleButton.setVisibility(View.GONE);
        
        bottomSheetSeekBar.setProgress(0);
        bottomSheetSeekBar.setVisibility(View.GONE);

        TextView seekBarPosition = mainActivity.getInstance().findViewById(R.id.seekbar_position);
        seekBarPosition.setVisibility(View.GONE);
        TextView audioLength = mainActivity.getInstance().findViewById(R.id.audio_length); // textView of the duration of the current audio file
        audioLength.setText(mainActivity.getInstance().getResources().getString(R.string.timestamp)); // setting the duration
        audioLength.setVisibility(View.GONE);

        SeekBar capstoneSeekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_seekbar);
        capstoneSeekBar.setProgress(0);
        capstoneSeekBar.setVisibility(View.GONE);
    }

    /**
     * Methods related to the seekBars in the bottom sheet
     */

    void initializeSeekBar() {
        MediaPlayer mediaPlayer = getMediaPlayer();
        if(mediaPlayer != null) {
            int mediaPos = mediaPlayer.getCurrentPosition();
            int mediaMax = mediaPlayer.getDuration();

            MainActivity mainActivity = new MainActivity();
            SeekBar seekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_seekbar);
            seekBar.setMax(mediaMax); // Set the Maximum range of the
            seekBar.setProgress(mediaPos);// set current progress to song's

            Handler seekBarHandler = new Handler();
            seekBarHandler.removeCallbacks(moveSeekBarThread);
            seekBarHandler.postDelayed(moveSeekBarThread, 100); // call the thread after 100 milliseconds
        }
    }

    public void initializeBottomSeekbar(MainActivity mainActivity) {
        SeekBar bottomSheetSeekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_seekbar);
        bottomSheetSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress, rawDuration; // progress is assigned the value of progressValue so it can be accessed by onStopTrackingTouch
                    double currentPositionRaw; // calculated by multiplying the raw duration (in milliseconds) of the audio file by the progress percentage / 100. E.G. 1000 * (50 / 100) = 500
                    String time; // The timestamp to be applied to the seekBarPosition textView

                    // The system has two states: before the mediaPlayer has started, and after. In the former, the range is o to 100, but in the
                    // latter it ranges from 0 to the total milliseconds of the audio file. Since 1 second of audio = 1,000 milliseconds, the range
                    // employed in the former state is easily exceeded. This boolean thus lets us check which state we're in and adjust the scale as necessary.
                    boolean isSeekBarStarted; // once the audio file is initialized, this boolean calculates ranges larger than 100. This is necessary for smoother progressBar movement.
                    MainActivity mainActivity = new MainActivity();
                    TextView seekBarPosition = mainActivity.getInstance().findViewById(R.id.seekbar_position);
                    AudioController audioController = new AudioController();
                    ActivePlaylistAdapter activePlaylistAdapter = new ActivePlaylistAdapter();
                    SeekBar bottomSheetCapstoneSeekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_seekbar);

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        isSeekBarStarted = SingletonController.getInstance().isSeekBarStarted();
                        if(!isSeekBarStarted) {
                            rawDuration = activePlaylistAdapter.getSelectedAudio().getRawDuration(); // length of the audio file in milliseconds

                            // calculated milliseconds elapsed by a percentage (progressValue ranges from 0 to 100 before the mediaPlayer initializes the audio file)
                            currentPositionRaw = (rawDuration * ((double) progressValue / 100));
                            time = audioController.milliSecondsToTimer((int) currentPositionRaw); // calculates time before starting the song
                        } else {
                            // calculates time before starting the song using the length of the audio file in milliseconds as the range
                            // e.g. a 10,000 millisecond song with a progressValue of 5,000 is halfway to completion
                            time = audioController.milliSecondsToTimer(progressValue);
                            progress = progressValue;
                        }
                        seekBarPosition.setText(time); // updates timestamp
                        bottomSheetCapstoneSeekBar.setProgress(progressValue); // sets progress of the capstone seekbar
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        SingletonController.getInstance().setSeekBarTracked(true);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        MediaPlayer mediaPlayer = getMediaPlayer();
                        if(!isSeekBarStarted) {
                            mediaPlayer.seekTo((int) currentPositionRaw);
                        } else {
                            mediaPlayer.seekTo(progress);
                        }
                        SingletonController.getInstance().setSeekBarTracked(false);
                    }
                }
        );
    }

    private Runnable moveSeekBarThread = new Runnable() {
        MediaPlayer mediaPlayer;
        MainActivity mainActivity = new MainActivity();
        SeekBar capstoneSeekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_capstone_seekbar);
        SeekBar bottomSheetSeekBar = mainActivity.getInstance().findViewById(R.id.bottom_sheet_seekbar);

        public void run() {
            mediaPlayer = getMediaPlayer();
            if(mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    if(!SingletonController.getInstance().isSeekBarStarted()) {
                        SingletonController.getInstance().setSeekBarStarted(true);
                    }

                    if(!SingletonController.getInstance().isSeekBarTracked()) {
                        int position = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();

                        capstoneSeekBar.setMax(duration);
                        capstoneSeekBar.setProgress(position);
                        bottomSheetSeekBar.setMax(duration);
                        bottomSheetSeekBar.setProgress(position);
                    }
                    Handler seekBarHandler = new Handler();
                    seekBarHandler.postDelayed(this, 100); // Looping the thread after 0.1 seconds
                }
            }
        }
    };
}