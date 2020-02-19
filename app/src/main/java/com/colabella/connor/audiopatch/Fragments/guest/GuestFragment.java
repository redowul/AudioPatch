package com.colabella.connor.audiopatch.Fragments.guest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Controllers.SingletonController;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;

public class GuestFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_guest, container, false);
        setViewData(root);
        return root;
    }

    public void setViewData(View view) {
        Button collapseAlbumCoverButton = view.findViewById(R.id.collapse_album_cover_button);
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            System.out.println(verticalOffset);
            if (Math.abs(verticalOffset)- appBarLayout1.getTotalScrollRange() == 0)
            {
                //  Collapsed

                System.out.println("Collapsed");
                collapseAlbumCoverButton.setOnClickListener(v -> {
                    appBarLayout.setExpanded(true);
                });
            }
            else
            {
                //Expanded
                collapseAlbumCoverButton.setOnClickListener(v -> {
                    appBarLayout.setExpanded(false);
                });

            }
        });

        if (SingletonController.getInstance().getActivePlaylistAdapter().getItemCount() > 0) {
            Audio selectedItem = SingletonController.getInstance().getActivePlaylistAdapter().getSelectedAudio();
            ImageView background = view.findViewById(R.id.album_art);
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
           /* TextView itemTitle = view.findViewById(R.id.playing_item_title);
            TextView itemArtist = view.findViewById(R.id.playing_item_artist);
            TextView itemSubmitter = view.findViewById(R.id.playing_item_album_submitter);
            TextView itemDuration = view.findViewById(R.id.playing_item_duration);

            String submittedBy = "Submitted by " + view.getResources().getString(R.string.submitter, selectedItem.getSubmitter());
            itemTitle.setText(selectedItem.getTitle());
            itemArtist.setText(selectedItem.getArtist());
            itemSubmitter.setText(submittedBy);
            itemDuration.setText(selectedItem.getDuration());
            */
        }
    }
}