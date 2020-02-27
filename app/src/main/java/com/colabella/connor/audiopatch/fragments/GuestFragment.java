package com.colabella.connor.audiopatch.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;

public class GuestFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_guest, container, false);
        setViewData(root);
        return root;
    }

    public void setViewData(View view) {
        ImageView background = view.findViewById(R.id.playing_item_background);
        MainActivity mainActivity = new MainActivity();
        if (background != null) {
            // For calculating the width of the screen
            DisplayMetrics displayMetrics = new DisplayMetrics();
            mainActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenW;
            screenW = displayMetrics.widthPixels; // get width of screen in pixels
            background.getLayoutParams().height = screenW;
            background.getLayoutParams().width = screenW;

            Bitmap appLogo = BitmapFactory.decodeResource(mainActivity.getInstance().getResources(), R.drawable.audiopatch_logo_square_blurrable);
            background.setImageBitmap(appLogo);
        }
    }

    public void updateGuestData(Bitmap albumArt, String filename, String artist, String duration, String submitter) {
        MainActivity mainActivity = new MainActivity();
        ImageView background = mainActivity.getInstance().findViewById(R.id.playing_item_background);
        TextView playingItemTitle  = mainActivity.getInstance().findViewById(R.id.playing_item_title);
        TextView playingItemArtist  = mainActivity.getInstance().findViewById(R.id.playing_item_artist);
        TextView playingItemDuration  = mainActivity.getInstance().findViewById(R.id.playing_item_duration);
        TextView playingItemSubmitter  = mainActivity.getInstance().findViewById(R.id.playing_item_submitter);

        if (background != null) {
            if (albumArt != null) {
                background.setImageBitmap(albumArt);
            } else {
                Bitmap appLogo = BitmapFactory.decodeResource(mainActivity.getInstance().getResources(), R.drawable.audiopatch_logo_square_blurrable);
                background.setImageBitmap(appLogo);
            }
        }

        playingItemTitle.setText(filename);
        playingItemArtist.setText(artist);
        playingItemDuration.setText(duration);

        String submittedBy = mainActivity.getInstance().getResources().getString(R.string.submitted_by);
        String submitMessage = submittedBy + " " + mainActivity.getInstance().getResources().getString(R.string.submitter, submitter);
        playingItemSubmitter.setText(submitMessage);
    }
}

