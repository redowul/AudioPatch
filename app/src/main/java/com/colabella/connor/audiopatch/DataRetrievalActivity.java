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
import java.util.List;

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
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleSearchView.setQuery("",true);
                hideSoftKeyboard(instance);
                AudioSingleton.getInstance().getSongAdapter().updateDataSet(AudioSingleton.getInstance().getAudioList());
                AudioSingleton.getInstance().getAlbumAdapter().updateDataSet(AudioSingleton.getInstance().getAlbumList());
                AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList());
                AudioSingleton.getInstance().getSongAdapter().notifyDataSetChanged();
                AudioSingleton.getInstance().getAlbumAdapter().notifyDataSetChanged();
                AudioSingleton.getInstance().getArtistAdapter().notifyDataSetChanged();
            }
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
                System.out.println(userInput);

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
                                AudioSingleton.getInstance().getSongAdapter().updateDataSet(filteredAudio);
                                AudioSingleton.getInstance().getSongAdapter().notifyDataSetChanged();
                            }
                        }
                        else filteredAudio.remove(item);

                        if (albumTitle.contains(userInput) || artist.contains(userInput)) {
                            List<Audio> searchedAlbum = AudioSingleton.getInstance().getAlbumByAlbumTitle(albumTitle);
                            if (searchedAlbum != null) {
                                if (!filteredAlbums.contains(searchedAlbum)) {
                                    filteredAlbums.add(searchedAlbum);
                                    AudioSingleton.getInstance().getAlbumAdapter().updateDataSet(filteredAlbums);
                                    AudioSingleton.getInstance().getAlbumAdapter().notifyDataSetChanged();
                                }
                            }
                        }
                        else {
                            List<Audio> searchedAlbum = AudioSingleton.getInstance().getAlbumByAlbumTitle(albumTitle);
                            filteredAlbums.remove(searchedAlbum);
                        }

                        if (artist.contains(userInput)) {
                            List<List<Audio>> searchedArtist = AudioSingleton.getInstance().getArtistByArtistName(artist);
                            if (searchedArtist != null) {
                                if (!filteredArtists.contains(searchedArtist)) {
                                    filteredArtists.add(searchedArtist);
                                    AudioSingleton.getInstance().getArtistAdapter().updateDataSet(filteredArtists);
                                    AudioSingleton.getInstance().getArtistAdapter().notifyDataSetChanged();
                                }
                            }
                        }
                        else {
                            List<List<Audio>> searchedArtist = AudioSingleton.getInstance().getArtistByArtistName(artist);
                            filteredArtists.remove(searchedArtist);
                        }
                    }
                }
                else {
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
    public void showPopup(final View view) {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, view);

        MenuInflater inflater = popup.getMenuInflater();
        MenuBuilder menuBuilder = new MenuBuilder(this);
        inflater.inflate(R.menu.dataretriever_options_menu, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(wrapper, menuBuilder, view);
        optionsMenu.show();

        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sort: {
                        menuItem.setTitle("Sort by...");
                        System.out.println("this one should be sort...");
                        return true;
                    }
                    case R.id.settings: {
                        System.out.println("and finally settings");
                        return true;
                    }
                    case R.id.sortMenu_alphabetical: {
                        System.out.println("item from submenu?");
                        return true;
                    }
                    case R.id.sortMenu_omegapsical: {
                        System.out.println("reverse of alphabetical");
                        return true;
                    }
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
        instance.finish();
        AudioSingleton.getInstance().getSongAdapter().updateDataSet(AudioSingleton.getInstance().getAudioList());
        AudioSingleton.getInstance().getAlbumAdapter().updateDataSet(AudioSingleton.getInstance().getAlbumList());
        AudioSingleton.getInstance().getArtistAdapter().updateDataSet(AudioSingleton.getInstance().getArtistList());
    }

    public void backButtonPressed(View view) { // Attached to song selection recyclerView xml.
        onBackPressed();
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}