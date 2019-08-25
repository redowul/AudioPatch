package com.colabella.connor.audiopatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.Fragments.GridDisplayFragment;
import com.colabella.connor.audiopatch.Fragments.SongListFragment;
import com.colabella.connor.audiopatch.RecyclerView.AlbumAdapter;
import com.colabella.connor.audiopatch.RecyclerView.ArtistAdapter;
import com.colabella.connor.audiopatch.RecyclerView.SongAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static android.widget.Toast.LENGTH_SHORT;

public class DataRetrievalActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static FloatingActionButton endActivityButton, snackBarButton, closeSoftKeyboardButton;
    private SearchView simpleSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataretriever);

        //AudioController audioController = new AudioController();
        //audioController.getAudioFilesFromDeviceStorage();

        ViewPager viewPager = findViewById(R.id.viewpager);
        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(3);
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = this.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Responsible for exiting the activity once an item has been clicked. Audio data retrieval/passing to the main program is handled independently.
        endActivityButton = findViewById(R.id.fab);
        endActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        snackBarButton = findViewById(R.id.snackbar_action);
        snackBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(view, "Selected file is corrupted", LENGTH_SHORT).setActionTextColor(Color.WHITE);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.recyclerViewPrimary));
                snackbar.show();
            }
        });

        closeSoftKeyboardButton = findViewById(R.id.close_soft_keyboard);
        closeSoftKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(DataRetrievalActivity.this);
            }
        });

        simpleSearchView = findViewById(R.id.searchView); // initialize search view
        simpleSearchView.setIconified(false);
        simpleSearchView.clearFocus();
        simpleSearchView.setFocusable(false);

        simpleSearchView.setOnCloseListener(new SearchView.OnCloseListener() { // handles the search bar's close button
            @Override
            public boolean onClose() {
                AppBarLayout appBarLayout = findViewById(R.id.appbar);
                appBarLayout.setExpanded(false, true);
                hideSoftKeyboard(DataRetrievalActivity.this);
                return true; // returning true will stop search view from collapsing
            }
        });

        // perform set on query text listener event
        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // do something on text submit
                return false;
            }

            // Handles album search feature. Filters albums on device by user input.
            @Override
            public boolean onQueryTextChange(String userInput) {
                AudioController audioController = new AudioController();
                SongAdapter songAdapter = audioController.getSongAdapter();
                AlbumAdapter albumAdapter = audioController.getAlbumAdapter();
                ArtistAdapter artistAdapter = audioController.getArtistAdapter();

                List<Audio> audioList = audioController.getAudioList();
                List<List<Audio>> albumList = audioController.getAlbumList();
                List<List<List<Audio>>> artistList = audioController.getArtistList();
                List<Audio> filteredAudio = new ArrayList<>();
                List<List<Audio>> filteredAlbums = new ArrayList<>();
                List<List<List<Audio>>> filteredArtists = new ArrayList<>();

                userInput = userInput.toLowerCase();
                if (userInput.length() > 0) {
                    for (Audio item : audioList) {
                        String title = item.getTitle();
                        String artist = item.getArtist();
                        String albumTitle = item.getAlbum();

                        title = title.toLowerCase();
                        artist = artist.toLowerCase();
                        albumTitle = albumTitle.toLowerCase();

                        if (title.toLowerCase().contains(userInput) || artist.toLowerCase().contains(userInput)) {
                            if (!filteredAudio.contains(item)) {
                                filteredAudio.add(item);
                            }
                        }
                        if (albumTitle.contains(userInput) || artist.contains(userInput)) {
                            List<Audio> searchedAlbum = audioController.getAlbumByAlbumTitle(albumTitle);
                            if (searchedAlbum != null) {
                                if (!filteredAlbums.contains(searchedAlbum)) {
                                    filteredAlbums.add(searchedAlbum);
                                }
                            }
                        }
                        if (artist.contains(userInput)) {
                            List<List<Audio>> searchedArtist = audioController.getArtistByArtistName(artist);
                            if (searchedArtist != null) {
                                if (!filteredArtists.contains(searchedArtist)) {
                                    filteredArtists.add(searchedArtist);
                                }
                            }
                        }
                    }
                }
                else {
                    filteredAudio = audioList;
                    filteredAlbums = albumList;
                    filteredArtists = artistList;
                }
                songAdapter.updateDataSet(filteredAudio);
                songAdapter.notifyDataSetChanged();

                albumAdapter.updateDataSet(filteredAlbums);
                albumAdapter.notifyDataSetChanged();

                artistAdapter.updateDataSet(filteredArtists);
                artistAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onResume() {
        super.onResume();
        if(endActivityButton == null){
            endActivityButton = findViewById(R.id.fab);
        }
        if(snackBarButton == null) {
            snackBarButton = findViewById(R.id.snackbar_action);
        }
        if(closeSoftKeyboardButton == null) {
            closeSoftKeyboardButton = findViewById(R.id.close_soft_keyboard);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear(); // Clear the Activity's bundle of the subsidiary fragments' bundles.
    }

    private void setupViewPager(ViewPager viewPager) {
        MainActivity.Adapter adapter = new MainActivity.Adapter(getSupportFragmentManager());

        String[] fragmentTitles = {"Artists", "Albums"}; // Titles to be displayed at the top of our ViewPager's gridDisplayFragment tabs

        for(int i = 0; i <= 1; i++) { // Loops twice, once for each item passed
            Bundle bundle = new Bundle();
            Fragment gridDisplayFragment = new GridDisplayFragment();
            bundle.putInt("key", i); // Int arguments get passed to a switch statement inside the fragment to determine which set of data to display
            gridDisplayFragment.setArguments(bundle);
            adapter.addFragment(gridDisplayFragment, fragmentTitles[i]); // Fragment, Title to display on fragment's ViewPager tab
        }
        adapter.addFragment(new SongListFragment(), "Songs");
        viewPager.setAdapter(adapter);
    }

    public void endActivity() {
        if(endActivityButton != null && snackBarButton != null) {
            endActivityButton.performClick();
        }
    }

    public void backButtonPressed(View view) { // Attached to song selection recyclerView xml.
        onBackPressed();
    }

    public void setCloseSoftKeyboardButton(View view) { closeSoftKeyboardButton.performClick(); }

    public void snackBarException(){ snackBarButton.performClick(); }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        simpleSearchView.clearFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(simpleSearchView != null) {
            simpleSearchView.setQuery("", true);
            simpleSearchView.clearFocus();
        }
    }
}