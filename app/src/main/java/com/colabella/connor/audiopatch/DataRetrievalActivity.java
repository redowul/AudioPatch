package com.colabella.connor.audiopatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.SearchView;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioSingleton;
import com.colabella.connor.audiopatch.Fragments.GridDisplayFragment;
import com.colabella.connor.audiopatch.Fragments.SongListFragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DataRetrievalActivity extends AppCompatActivity {

    private static DataRetrievalActivity instance;

    public DataRetrievalActivity getInstance() {
        return instance;
    }

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

        instance = this;

        final SearchView simpleSearchView = findViewById(R.id.searchView);
        ImageView closeButton = simpleSearchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(view -> {
            simpleSearchView.setQuery("", true);
            hideSoftKeyboard(instance);
            AudioSingleton.getInstance().getSongAdapter().updateDataSet(AudioSingleton.getInstance().getAudioList());
            AudioSingleton.getInstance().getAlbumAdapter().updateDataSet(AudioSingleton.getInstance().getAlbumList());
            AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList());
            AudioSingleton.getInstance().getSongAdapter().notifyDataSetChanged();
            AudioSingleton.getInstance().getAlbumAdapter().notifyDataSetChanged();
            AudioSingleton.getInstance().getArtistAdapter().notifyDataSetChanged();
        });

        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // do something on text submit
                return false;
            }

            // Handles album search feature. Filters albums on device by user input.
            @Override
            public boolean onQueryTextChange(String userInput) {
                List<Audio> audioList = AudioSingleton.getInstance().getAudioList();
                List<Audio> filteredAudio = new ArrayList<>();
                List<List<Audio>> filteredAlbums = new ArrayList<>();
                List<List<List<Audio>>> filteredArtists = new ArrayList<>();

                userInput = userInput.toLowerCase();
                if (userInput.length() > 0) {
                    for (Audio item : audioList) {
                        String title = item.getTitle().toLowerCase();
                        String artist = item.getArtist().toLowerCase();
                        String albumTitle = item.getAlbum().toLowerCase();

                        if (title.contains(userInput) || artist.contains(userInput)) {
                            if (!filteredAudio.contains(item)) {
                                filteredAudio.add(item);
                            }
                        } else {
                            filteredAudio.remove(item);
                        }

                        if (albumTitle.contains(userInput) || artist.contains(userInput)) {
                            List<Audio> searchedAlbum = AudioSingleton.getInstance().getAlbumByAlbumTitle(albumTitle);
                            if (searchedAlbum != null) {
                                if (!filteredAlbums.contains(searchedAlbum)) {
                                    filteredAlbums.add(searchedAlbum);
                                }
                            }
                        } else {
                            List<Audio> searchedAlbum = AudioSingleton.getInstance().getAlbumByAlbumTitle(albumTitle);
                            filteredAlbums.remove(searchedAlbum);
                        }

                        if (artist.contains(userInput)) {
                            List<List<Audio>> searchedArtist = AudioSingleton.getInstance().getArtistByArtistName(artist);
                            if (searchedArtist != null) {
                                if (!filteredArtists.contains(searchedArtist)) {
                                    filteredArtists.add(searchedArtist);
                                }
                            }
                        } else {
                            List<List<Audio>> searchedArtist = AudioSingleton.getInstance().getArtistByArtistName(artist);
                            filteredArtists.remove(searchedArtist);
                        }
                    }
                    AudioSingleton.getInstance().getSongAdapter().updateDataSet(filteredAudio);
                    AudioSingleton.getInstance().getSongAdapter().notifyDataSetChanged();
                    AudioSingleton.getInstance().getAlbumAdapter().updateDataSet(filteredAlbums);
                    AudioSingleton.getInstance().getAlbumAdapter().notifyDataSetChanged();
                    AudioSingleton.getInstance().getArtistAdapter().updateDataSet(filteredArtists);
                    AudioSingleton.getInstance().getArtistAdapter().notifyDataSetChanged();
                } else {
                    AudioSingleton.getInstance().getSongAdapter().updateDataSet(AudioSingleton.getInstance().getAudioList());
                    AudioSingleton.getInstance().getAlbumAdapter().updateDataSet(AudioSingleton.getInstance().getAlbumList());
                    AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList());
                    AudioSingleton.getInstance().getSongAdapter().notifyDataSetChanged();
                    AudioSingleton.getInstance().getAlbumAdapter().notifyDataSetChanged();
                    AudioSingleton.getInstance().getArtistAdapter().notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    @SuppressLint({"NewApi", "RestrictedApi"})
    public void showPopupSortSubmenu(final View view) {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu);

        final PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        MenuBuilder menuBuilder = new MenuBuilder(this);
        inflater.inflate(R.menu.dataretriever_options_sort_submenu, menuBuilder);
        final MenuPopupHelper optionsMenu = new MenuPopupHelper(wrapper, menuBuilder, view);
        optionsMenu.setGravity(Gravity.END);
        optionsMenu.show(100, 50);
        ViewPager viewPager = findViewById(R.id.viewpager);

        if (viewPager != null) {
            if (viewPager.getCurrentItem() == 0) { // Artists
                menuBuilder.findItem(R.id.sortMenu_artist).setVisible(false);
                menuBuilder.findItem(R.id.sortMenu_album).setVisible(false);
                menuBuilder.findItem(R.id.sortMenu_shortestDuration).setVisible(false);
                menuBuilder.findItem(R.id.sortMenu_longestDuration).setVisible(false);
                menuBuilder.findItem(R.id.sortMenu_totalAlbums).setVisible(true);
            } else if (viewPager.getCurrentItem() == 1) { // Albums
                menuBuilder.findItem(R.id.sortMenu_artist).setVisible(true);
                menuBuilder.findItem(R.id.sortMenu_album).setVisible(false);
                menuBuilder.findItem(R.id.sortMenu_shortestDuration).setVisible(false);
                menuBuilder.findItem(R.id.sortMenu_longestDuration).setVisible(false);
                menuBuilder.findItem(R.id.sortMenu_totalAlbums).setVisible(false);
            } else { // Songs
                menuBuilder.findItem(R.id.sortMenu_artist).setVisible(true);
                menuBuilder.findItem(R.id.sortMenu_album).setVisible(true);
                menuBuilder.findItem(R.id.sortMenu_shortestDuration).setVisible(true);
                menuBuilder.findItem(R.id.sortMenu_longestDuration).setVisible(true);
                menuBuilder.findItem(R.id.sortMenu_totalAlbums).setVisible(false);
            }
        }

        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
                if (viewPager != null) {
                    switch (menuItem.getItemId()) {
                        case R.id.sortMenu_alphabetical: {
                            if (viewPager.getCurrentItem() == 0) { // Artists
                                Collections.sort(AudioSingleton.getInstance().getArtistList(), Audio.sortArtistsAlphabeticallyComparator);
                                AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList());
                                AudioSingleton.getInstance().getArtistAdapter().notifyDataSetChanged();
                            } else if (viewPager.getCurrentItem() == 1) { // Albums
                                Collections.sort(AudioSingleton.getInstance().getAlbumList(), Audio.sortAlbumsAlphabeticallyComparator);
                                AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList());
                                AudioSingleton.getInstance().getArtistAdapter().notifyDataSetChanged();
                            } else { // Songs
                                Collections.sort(AudioSingleton.getInstance().getAudioList(), Audio.sortAudioAlphabeticallyComparator);
                            }
                        }
                        break;
                        case R.id.sortMenu_omegapsical: {
                            if (viewPager.getCurrentItem() == 0) { // Artists
                                Collections.sort(AudioSingleton.getInstance().getArtistList(), Audio.sortArtistsOmegapsicallyComparator);
                                AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList());
                                AudioSingleton.getInstance().getArtistAdapter().notifyDataSetChanged();
                            } else if (viewPager.getCurrentItem() == 1) { // Albums
                                Collections.sort(AudioSingleton.getInstance().getAlbumList(), Audio.sortAlbumsOmegapsicallyComparator);
                            } else { // Songs
                                Collections.sort(AudioSingleton.getInstance().getAudioList(), Audio.sortAudioOmegapsicallyComparator);
                            }
                        }
                        break;
                        case R.id.sortMenu_artist: {
                            if (viewPager.getCurrentItem() == 1) { // Albums
                                Collections.sort(AudioSingleton.getInstance().getAlbumList(), Audio.sortAlbumsAlphabeticallyComparator);
                                Collections.sort(AudioSingleton.getInstance().getAlbumList(), Audio.sortAlbumsByArtistsComparator);
                            } else if (viewPager.getCurrentItem() == 2) { // Songs
                                Audio.sortAudioByArtist();
                            }
                        }
                        break;
                        case R.id.sortMenu_album: {
                            if (viewPager.getCurrentItem() == 2) { // Songs
                                Audio.sortAudioByAlbum();
                            }
                        }
                        break;
                        case R.id.sortMenu_shortestDuration: {
                            Collections.sort(AudioSingleton.getInstance().getAudioList(), Audio.sortAudioByShortestDurationComparator);
                        }
                        break;
                        case R.id.sortMenu_longestDuration: {
                            Collections.sort(AudioSingleton.getInstance().getAudioList(), Audio.sortAudioByLongestDurationComparator);
                        }
                        break;
                        case R.id.sortMenu_totalAlbums: {
                            if (viewPager.getCurrentItem() == 0) { // Artists
                                Collections.sort(AudioSingleton.getInstance().getArtistList(), Audio.sortArtistsAlphabeticallyComparator);
                                Collections.sort(AudioSingleton.getInstance().getArtistList(), Audio.sortArtistsByTotalAlbumsComparator);
                                AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList());
                                AudioSingleton.getInstance().getArtistAdapter().notifyDataSetChanged();
                            }
                        }
                        break;
                    }
                }
                AudioSingleton.getInstance().getSongAdapter().updateDataSet(AudioSingleton.getInstance().getAudioList());
                AudioSingleton.getInstance().getSongAdapter().notifyDataSetChanged();
                AudioSingleton.getInstance().getAlbumAdapter().updateDataSet(AudioSingleton.getInstance().getAlbumList());
                AudioSingleton.getInstance().getAlbumAdapter().notifyDataSetChanged();
                return false;
            }

            @Override
            public void onMenuModeChange(MenuBuilder menuBuilder) {

            }
        });
    }

    @SuppressLint({"NewApi", "RestrictedApi"})
    public void showPopup(final View view) {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu);

        final PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        MenuBuilder menuBuilder = new MenuBuilder(this);
        inflater.inflate(R.menu.dataretriever_options_menu, menuBuilder);
        final MenuPopupHelper optionsMenu = new MenuPopupHelper(wrapper, menuBuilder, view);
        optionsMenu.setGravity(Gravity.END);
        optionsMenu.show(100, 50);

        // Inflate the menu; this adds items to the action bar if it is present.
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sort: {
                        showPopupSortSubmenu(view);
                    }
                    break;
                    case R.id.settings: {

                    }
                    break;
                }
                return false;
            }

            @Override
            public void onMenuModeChange(MenuBuilder menuBuilder) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear(); // Clear the Activity's bundle of the subsidiary fragments' bundles.
    }

    private void setupViewPager(ViewPager viewPager) {
        MainActivity.Adapter adapter = new MainActivity.Adapter(getSupportFragmentManager());
        String[] fragmentTitles = {"Artists", "Albums"}; // Titles to be displayed at the top of our ViewPager's gridDisplayFragment tabs

        for (int i = 0; i <= 1; i++) { // Loops twice, once for each item passed
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
        instance.finish();
        AudioSingleton.getInstance().getSongAdapter().updateDataSet(AudioSingleton.getInstance().getAudioList());
        AudioSingleton.getInstance().getAlbumAdapter().updateDataSet(AudioSingleton.getInstance().getAlbumList());
        AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList());
    }

    public void backButtonPressed(View view) { // Attached to song selection recyclerView xml.
        onBackPressed();
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }
}