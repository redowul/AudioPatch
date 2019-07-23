package com.colabella.connor.audiopatch;

import android.annotation.SuppressLint;
import android.app.Activity;
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
                SongAdapter songAdapter = new SongAdapter();
                AlbumAdapter albumAdapter = new AlbumAdapter();
                ArtistAdapter artistAdapter = new ArtistAdapter();

                List<Audio> audioList = audioController.getAudioList();
                List<List<Audio>> albumList = audioController.getAlbumList();
                List<List<List<Audio>>> artistList = audioController.getArtistList();

                List<Audio> filteredAudio = new ArrayList<>(audioList);
                List<List<Audio>> filteredAlbums = new ArrayList<>(albumList);
                List<List<List<Audio>>> filteredArtists = new ArrayList<>(artistList);

                userInput = userInput.toLowerCase();

                for (int i = 0; i < filteredAudio.size(); i++) {
                    String title = filteredAudio.get(i).getTitle();
                    String artist = filteredAudio.get(i).getArtist();

                    title = title.toLowerCase();
                    artist = artist.toLowerCase();
                    if (userInput.length() > 0) {
                        if (!title.contains(userInput) && !artist.contains(userInput)) { // If userInput does not match the name of the album or the name of the artist, remove album from the list.
                            filteredAudio.remove(i);
                            songAdapter.updateDataSet(filteredAudio);
                            i = -1;
                        }
                    }
                    else { songAdapter.updateDataSet(audioList); }
                }

                for (int i = 0; i < filteredAlbums.size(); i++) {
                    String artist = filteredAlbums.get(i).get(0).getArtist();
                    String album = filteredAlbums.get(i).get(0).getAlbum();
                    artist = artist.toLowerCase();
                    album = album.toLowerCase();
                    if (userInput.length() > 0) {
                        if (!album.contains(userInput) && !artist.contains(userInput)) { // If userInput does not match the name of the album or the name of the artist, remove album from the list.
                            filteredAlbums.remove(i);
                            albumAdapter.updateDataSet(filteredAlbums);
                            i = -1;
                        }
                    }
                    else { albumAdapter.updateDataSet(albumList); }
                }

                for (int i = 0; i < filteredArtists.size(); i++) {
                    String artist = filteredArtists.get(i).get(0).get(0).getArtist();
                    String album = filteredArtists.get(i).get(0).get(0).getAlbum();
                    artist = artist.toLowerCase();
                    album = album.toLowerCase();
                    if (userInput.length() > 0) {
                        if (!album.contains(userInput) && !artist.contains(userInput)) { // If userInput does not match the name of the album or the name of the artist, remove album from the list.
                            filteredArtists.remove(i);
                            artistAdapter.updateDataSet(filteredArtists);
                            i = -1;
                        }
                    }
                    else { artistAdapter.updateDataSet(audioController.getArtistList()); }
                }
                return true;
            }
        });
    }

    @Override
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