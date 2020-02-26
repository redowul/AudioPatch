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

import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.controllers.SingletonController;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;

public class GuestFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_guest, container, false);
        setViewData(root);
        return root;
    }

    public void setViewData(View view) {
        if (SingletonController.getInstance().getActivePlaylistAdapter().getItemCount() > 0) {
            Audio selectedItem = SingletonController.getInstance().getActivePlaylistAdapter().getSelectedAudio();
            ImageView background = view.findViewById(R.id.playing_item_background);
            if (background != null) {
                // For calculating the width of the screen
                DisplayMetrics displayMetrics = new DisplayMetrics();
                MainActivity mainActivity = new MainActivity();
                mainActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenW;
                screenW = displayMetrics.widthPixels; // get width of screen in pixels
                background.getLayoutParams().height = screenW;
                background.getLayoutParams().width = screenW;

                if (selectedItem.getAlbumArt() != null) {
                    background.setImageBitmap(selectedItem.getAlbumArt());
                } else {
                    Bitmap appLogo = BitmapFactory.decodeResource(mainActivity.getInstance().getResources(), R.drawable.audiopatch_logo_square_blurrable);
                    background.setImageBitmap(appLogo);
                }
            }

            TextView itemTitle = view.findViewById(R.id.playing_item_title);
            TextView itemArtist = view.findViewById(R.id.playing_item_artist);
            TextView itemSubmitter = view.findViewById(R.id.playing_item_submitter);
            TextView itemDuration = view.findViewById(R.id.playing_item_duration);

            String submittedBy = view.getResources().getString(R.string.submitted_by);
            String submitMessage = submittedBy + " " + view.getResources().getString(R.string.submitter, selectedItem.getSubmitter());

            itemTitle.setText(selectedItem.getTitle());
            itemArtist.setText(selectedItem.getArtist());
            itemSubmitter.setText(submitMessage);
            itemDuration.setText(selectedItem.getDuration());
        }
    }
}