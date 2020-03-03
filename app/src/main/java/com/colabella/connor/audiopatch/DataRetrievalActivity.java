package com.colabella.connor.audiopatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.SearchView;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.controllers.SingletonController;
import com.colabella.connor.audiopatch.fragments.GridDisplayFragment;
import com.colabella.connor.audiopatch.fragments.SongListFragment;
import com.colabella.connor.audiopatch.nearbyconnections.PayloadController;
import com.colabella.connor.audiopatch.recyclerview.ActivePlaylistAdapter;
import com.colabella.connor.audiopatch.recyclerview.SongAdapter;

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
            SingletonController.getInstance().getSongAdapter().updateDataSet(SingletonController.getInstance().getAudioList());
            SingletonController.getInstance().getAlbumAdapter().updateDataSet(SingletonController.getInstance().getAlbumList());
            SingletonController.getInstance().getArtistAdapter().updateDataSet(SingletonController.getInstance().getArtistList());
            SingletonController.getInstance().getSongAdapter().notifyDataSetChanged();
            SingletonController.getInstance().getAlbumAdapter().notifyDataSetChanged();
            SingletonController.getInstance().getArtistAdapter().notifyDataSetChanged();
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
                filterAudio(userInput);
                SingletonController.getInstance().setFilter(userInput);
                return true;
            }
        });

        // Confirmation button
        FloatingActionButton confirmationButton = findViewById(R.id.confirmation_button);
        confirmationButton.setOnClickListener(view -> {
            Audio item = SingletonController.getInstance().getSelectedAudio();

            if (item != null) {
                item.setSelected(false);
                endActivity();

                PayloadController payloadController = new PayloadController();
                if (SingletonController.getInstance().getEndpointIdList() != null) {
                    if (SingletonController.getInstance().getEndpointIdList().size() > 0) {
                        if (SingletonController.getInstance().isGuest()) {
                            String endpointId = SingletonController.getInstance().getEndpointIdList().get(0);

                            MainActivity mainActivity = new MainActivity();
                            Context context = mainActivity.getInstance();

                            payloadController.sendAudio(endpointId, item, context);
                            return;
                        }
                    }
                }
                ActivePlaylistAdapter activePlaylistAdapter = SingletonController.getInstance().getActivePlaylistAdapter();
                activePlaylistAdapter.addItem(Audio.copy(item));
                activePlaylistAdapter.notifyDataSetChanged();

                SingletonController.getInstance().setSelectedAudio(null);
            }
        });
        confirmationButton.hide();
    }

    private void filterAudio(String userInput) {
        List<Audio> audioList = SingletonController.getInstance().getAudioList();
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
                    List<Audio> searchedAlbum = SingletonController.getInstance().getAlbumByAlbumTitle(albumTitle);
                    if (searchedAlbum != null) {
                        if (!filteredAlbums.contains(searchedAlbum)) {
                            filteredAlbums.add(searchedAlbum);
                        }
                    }
                } else {
                    List<Audio> searchedAlbum = SingletonController.getInstance().getAlbumByAlbumTitle(albumTitle);
                    filteredAlbums.remove(searchedAlbum);
                }

                if (artist.contains(userInput)) {
                    List<List<Audio>> searchedArtist = SingletonController.getInstance().getArtistByArtistName(artist);
                    if (searchedArtist != null) {
                        if (!filteredArtists.contains(searchedArtist)) {
                            filteredArtists.add(searchedArtist);
                        }
                    }
                } else {
                    List<List<Audio>> searchedArtist = SingletonController.getInstance().getArtistByArtistName(artist);
                    filteredArtists.remove(searchedArtist);
                }
            }
            SingletonController.getInstance().getSongAdapter().updateDataSet(filteredAudio);
            SingletonController.getInstance().getSongAdapter().notifyDataSetChanged();
            SingletonController.getInstance().getAlbumAdapter().updateDataSet(filteredAlbums);
            SingletonController.getInstance().getAlbumAdapter().notifyDataSetChanged();
            SingletonController.getInstance().getArtistAdapter().updateDataSet(filteredArtists);
            SingletonController.getInstance().getArtistAdapter().notifyDataSetChanged();
        } else {
            SingletonController.getInstance().getSongAdapter().updateDataSet(SingletonController.getInstance().getAudioList());
            SingletonController.getInstance().getAlbumAdapter().updateDataSet(SingletonController.getInstance().getAlbumList());
            SingletonController.getInstance().getArtistAdapter().updateDataSet(SingletonController.getInstance().getArtistList());
            SingletonController.getInstance().getSongAdapter().notifyDataSetChanged();
            SingletonController.getInstance().getAlbumAdapter().notifyDataSetChanged();
            SingletonController.getInstance().getArtistAdapter().notifyDataSetChanged();
        }
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
        optionsMenu.show(100, 10);
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
                                Collections.sort(SingletonController.getInstance().getArtistList(), Audio.sortArtistsAlphabeticallyComparator);
                                SingletonController.getInstance().getArtistAdapter().updateDataSet(SingletonController.getInstance().getArtistList());
                                SingletonController.getInstance().getArtistAdapter().notifyDataSetChanged();
                            } else if (viewPager.getCurrentItem() == 1) { // Albums
                                Collections.sort(SingletonController.getInstance().getAlbumList(), Audio.sortAlbumsAlphabeticallyComparator);
                                SingletonController.getInstance().getArtistAdapter().updateDataSet(SingletonController.getInstance().getArtistList());
                                SingletonController.getInstance().getArtistAdapter().notifyDataSetChanged();
                            } else { // Songs
                                Collections.sort(SingletonController.getInstance().getAudioList(), Audio.sortAudioAlphabeticallyComparator);
                            }
                        }
                        break;
                        case R.id.sortMenu_omegapsical: {
                            if (viewPager.getCurrentItem() == 0) { // Artists
                                Collections.sort(SingletonController.getInstance().getArtistList(), Audio.sortArtistsOmegapsicallyComparator);
                                SingletonController.getInstance().getArtistAdapter().updateDataSet(SingletonController.getInstance().getArtistList());
                                SingletonController.getInstance().getArtistAdapter().notifyDataSetChanged();
                            } else if (viewPager.getCurrentItem() == 1) { // Albums
                                Collections.sort(SingletonController.getInstance().getAlbumList(), Audio.sortAlbumsOmegapsicallyComparator);
                            } else { // Songs
                                Collections.sort(SingletonController.getInstance().getAudioList(), Audio.sortAudioOmegapsicallyComparator);
                            }
                        }
                        break;
                        case R.id.sortMenu_artist: {
                            if (viewPager.getCurrentItem() == 1) { // Albums
                                Collections.sort(SingletonController.getInstance().getAlbumList(), Audio.sortAlbumsAlphabeticallyComparator);
                                Collections.sort(SingletonController.getInstance().getAlbumList(), Audio.sortAlbumsByArtistsComparator);
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
                            Collections.sort(SingletonController.getInstance().getAudioList(), Audio.sortAudioByShortestDurationComparator);
                        }
                        break;
                        case R.id.sortMenu_longestDuration: {
                            Collections.sort(SingletonController.getInstance().getAudioList(), Audio.sortAudioByLongestDurationComparator);
                        }
                        break;
                        case R.id.sortMenu_totalAlbums: {
                            if (viewPager.getCurrentItem() == 0) { // Artists
                                Collections.sort(SingletonController.getInstance().getArtistList(), Audio.sortArtistsAlphabeticallyComparator);
                                Collections.sort(SingletonController.getInstance().getArtistList(), Audio.sortArtistsByTotalAlbumsComparator);
                                SingletonController.getInstance().getArtistAdapter().updateDataSet(SingletonController.getInstance().getArtistList());
                                SingletonController.getInstance().getArtistAdapter().notifyDataSetChanged();
                            }
                        }
                        break;
                    }
                }
                SingletonController.getInstance().getSongAdapter().updateDataSet(SingletonController.getInstance().getAudioList());
                SingletonController.getInstance().getSongAdapter().notifyDataSetChanged();
                SingletonController.getInstance().getAlbumAdapter().updateDataSet(SingletonController.getInstance().getAlbumList());
                SingletonController.getInstance().getAlbumAdapter().notifyDataSetChanged();

                // Filters all items by user input after they've been sorted
                String filter = SingletonController.getInstance().getFilter();
                if (filter != null) {
                    filterAudio(filter);
                }
                return false;
            }

            @Override
            public void onMenuModeChange(MenuBuilder menuBuilder) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dataretriever_options_menu, menu);
        return true;
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
        optionsMenu.show(100, 10);

        // Inflate the menu; this adds items to the action bar if it is present.
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sort: {
                        showPopupSortSubmenu(view);
                    }
                    break;
                    /*case R.id.settings: {

                    }
                    break;*/
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
        Adapter adapter = new Adapter(getSupportFragmentManager());
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

    // Used for ViewPager
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        Adapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    public void endActivity() {
        instance.finish();
        SingletonController.getInstance().getSongAdapter().updateDataSet(SingletonController.getInstance().getAudioList());
        SingletonController.getInstance().getAlbumAdapter().updateDataSet(SingletonController.getInstance().getAlbumList());
        SingletonController.getInstance().getArtistAdapter().updateDataSet(SingletonController.getInstance().getArtistList());

        SingletonController.getInstance().getSongAdapter().deselectAll();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if(SingletonController.getInstance().getSelectedAudio() != null) {
                SingletonController.getInstance().getSongAdapter().deselectAll();
            }
            endActivity();
        }
        else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void backButtonPressed(View view) { // Attached to a button in fragment_album_song_selection.xml; does not override the back button
        onBackPressed();
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }
}