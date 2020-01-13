package com.colabella.connor.audiopatch;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.Audio.AudioSingleton;
import com.colabella.connor.audiopatch.NearbyConnections.NearbyConnectionsController;
import com.colabella.connor.audiopatch.RecyclerView.ActivePlaylistAdapter;
import com.colabella.connor.audiopatch.RecyclerView.ActivePlaylistController;
import com.colabella.connor.audiopatch.RecyclerView.MainDrawerAdapter;
import com.colabella.connor.audiopatch.RecyclerView.SwipeAndDragHelper;
import com.qhutch.bottomsheetlayout.BottomSheetLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {

    private static MainActivity instance;

    public MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        instance = this;

        Toolbar toolbar = findViewById(R.id.toolbar_top);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

    // drawer.openDrawer(GravityCompat.START); //TODO open and lock the drawer on boot to force the user to select advertise or discover
        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

        initializeRecyclerView();
        initializeBottomSheet();

        ImageView header = findViewById(R.id.drawer_header);
        header.setImageResource(R.drawable.audiopatch_header_transparent);
        initializeNavigationView();

        //TODO place this automatic loader inside an 'if' that triggers only when permissions authorize it

        // Ensures the data from the device storage is only retrieved once, when the list that data is stored in is empty
        if (AudioSingleton.getInstance().getAudioList() != null) {
            if (AudioSingleton.getInstance().getAudioList().size() == 0) {
                AudioController audioController = new AudioController();
                audioController.getAudioFilesFromDeviceStorage();
            }
        }

        if (AudioSingleton.getInstance().getActivePlaylistAdapter().getItemCount() > 0) {
            Audio selectedItem = AudioSingleton.getInstance().getActivePlaylistAdapter().getSelectedAudio();

            ActivePlaylistController activePlaylistController = new ActivePlaylistController();
            activePlaylistController.alterBottomSheet(selectedItem);
            activePlaylistController.togglePlayButtonState();
        }
    }

    private void initializeNavigationView() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Button openDrawerButton = findViewById(R.id.open_drawer_button);
        openDrawerButton.setOnClickListener(view -> drawer.openDrawer(GravityCompat.START)); // close the sheet if pressed

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.drawer_recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);

        MainDrawerAdapter mainDrawerAdapter = new MainDrawerAdapter();
        mainDrawerAdapter.addItem("Home", true);
        mainDrawerAdapter.addItem("Settings", false);
        mainDrawerAdapter.addItem("About", false);
        recyclerView.setAdapter(mainDrawerAdapter);

    /*    DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        String colorPrimary = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary) & 0x00ffffff);
        String textColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.textColor) & 0x00ffffff);
        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked},  // checked
                },
                new int[] {
                        Color.parseColor(textColor),
                        Color.parseColor(colorPrimary),
                });
        navigationView.setItemTextColor(colorStateList);
        navigationView.setItemIconTintList(colorStateList);
        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home: {
                    System.out.println("Test 1");
                    menuItem.setChecked(true);
                }
                break;
                case R.id.nav_downloads: {
                    System.out.println("Test 1 2");
                    menuItem.setChecked(true);
                }
                break;
                case R.id.nav_history: {
                    System.out.println("Test 1 2 3");
                    menuItem.setChecked(true);
                }
                break;
                case R.id.nav_settings: {
                    System.out.println("Test 1 2 3 4");
                    menuItem.setChecked(true);
                }
                break;
                case R.id.nav_about: {
                    System.out.println("Test 1 2 3 4 5");
                    menuItem.setChecked(true);
                }
                break;
                case R.id.nav_advertise: {
                    if (!menuItem.isChecked()) {
                        MenuItem item = navigationView.getMenu().findItem(R.id.nav_discover);
                        item.setChecked(false);
                        System.out.println("Test 1 2 3 4 5 6");
                        menuItem.setChecked(true);
                    } else {
                        menuItem.setChecked(false);
                    }
                }
                break;
                case R.id.nav_discover: {
                    if (!menuItem.isChecked()) {
                        MenuItem item = navigationView.getMenu().findItem(R.id.nav_advertise);
                        item.setChecked(false);
                        System.out.println("Test 1 2 3 4 5 6 7");
                        menuItem.setChecked(true);
                    } else {
                        menuItem.setChecked(false);
                    }
                    //drawer.closeDrawer(GravityCompat.START);
                    //Toast.makeText(instance, "You may now close the drawer.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            return true;
        });
     */
    }

    //TODO move these to their own class, no need for them to take up space here
    /** Initialization methods **/
    private void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ActivePlaylistAdapter recyclerViewAdapter = AudioSingleton.getInstance().getActivePlaylistAdapter();

        SwipeAndDragHelper swipeAndDragHelper = new SwipeAndDragHelper(recyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAndDragHelper);
        recyclerViewAdapter.setTouchHelper(itemTouchHelper);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initializeBottomSheet() {
        // For calculating the width of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenW = displayMetrics.widthPixels; // get width of screen in pixels
        BottomSheetLayout layout = findViewById(R.id.bottom_sheet_layout);
        layout.getLayoutParams().height = (int) (screenW * .75); // set height of the bottom sheet to 75% the width of the screen. (Dynamic, screenW value depends on the size of device)

        layout.setOnProgressListener(progress -> {
            RelativeLayout bottomSheetLayoutCapstone = findViewById(R.id.bottom_sheet_layout_capstone); // needed for calculating bottom Y value of the capstone

            double capstoneMaxY = bottomSheetLayoutCapstone.getBottom(); // bottom Y value of the capstone
            double minY = layout.getTop(); // upper Y value of the layout

            // bottom Y value of the layout minus the bottom Y value of the capstone
            double maxY = layout.getBottom() - capstoneMaxY; // (accounts for the difference of ~30% caused by the capstone blocking the bottom sheet from resting flush on the bottom of the screen)
            double currentY = layout.getY(); // current Y value of the top of the sheet

            // used for calculating our percentage value below; MinY, maxY, and currentY are values relative to the entire screen, but we only want to know the Y value relative to the bottom sheet
            // therefore we subtract the minimum Y value from the maximum Y value to get the difference, which is the total size of the bottom sheet
            double bottomSheetSize = maxY - minY;
            double currentAdjustedY = currentY - minY; // Y position relative to the top and bottom Y values of the bottom sheet, not the entire screen

            double percentage = (currentAdjustedY / bottomSheetSize) * 100; // used for calculating the percentage needed for rotation and transparency
            int alpha = (int) ((percentage / 100) * 255); // calculates level of transparency to be applied
            int degreesOfRotation = (int) (((percentage / 100) * 180) + 180); // used for calculating degrees of rotation to be applied to our expandCollapseBottomSheetButton

            float capstoneAlpha = (float) (percentage * .01); // range between 0.0 and 1.0 used for setting the transparency of the relativeLayout
            bottomSheetLayoutCapstone.setAlpha(capstoneAlpha); // sets the transparency of the capstone

            /* Bottom Sheet Capstone Text blocks */
            TextView bottomSheetCapstoneTitle = findViewById(R.id.bottom_sheet_capstone_title);
            TextView bottomSheetCapstoneArtist = findViewById(R.id.bottom_sheet_capstone_artist);
            if (bottomSheetCapstoneTitle.getVisibility() == View.VISIBLE && bottomSheetCapstoneArtist.getVisibility() == View.VISIBLE) {
                bottomSheetCapstoneTitle.setTextColor(Color.argb(alpha, 255, 255, 255));  // transparency of capstone song title
                bottomSheetCapstoneArtist.setTextColor(Color.argb(alpha, 255, 255, 255)); // transparency of capstone artist title
            }

            /* Bottom Sheet Capstone album cover */
            ImageView bottomSheetLayoutCapstoneAlbumCover = findViewById(R.id.bottom_sheet_current_album_cover_small);
            if (bottomSheetLayoutCapstoneAlbumCover.getDrawable() != null) {
                bottomSheetLayoutCapstoneAlbumCover.getDrawable().setAlpha(alpha); // set the alpha of the bitmap overlain on top of the image view
            }

            /* Bottom Sheet button (expansion/collapse control) */
            Button expandCollapseBottomSheetButton = findViewById(R.id.expand_bottom_sheet_button);
            expandCollapseBottomSheetButton.setRotation(degreesOfRotation);
            if (percentage == 0) { // bottom sheet expanded state
                expandCollapseBottomSheetButton.setOnClickListener(view -> layout.collapse()); // close the sheet if pressed
            } else {
                expandCollapseBottomSheetButton.setOnClickListener(view -> layout.expand()); // open the sheet if pressed
            }

            /* Bottom Sheet collapsed state; this if statement is here so that we aren't constantly blurring the image as the bottom sheet moves */
            if (percentage == 100) {
                ActivePlaylistAdapter activePlaylistAdapter = new ActivePlaylistAdapter();
                boolean isItemSelected = activePlaylistAdapter.isItemSelected();
                if(!isItemSelected) {
                    ActivePlaylistController activePlaylistController = new ActivePlaylistController();
                    activePlaylistController.resetBottomSheet();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Handles all button clicks that occur in context_main.xml.
    public void onBottomToolbarItemClick(View view) {
        ActivePlaylistController activePlaylistController = new ActivePlaylistController();
        activePlaylistController.determineButtonSelected(getResources().getResourceEntryName(view.getId()), view); // Passes determineButtonSelected() the String ID of the pressed button.
    }

    //Enables audio selection
    public void selectAudioFromStorage(View view) {
        Intent myIntent = new Intent(MainActivity.this, DataRetrievalActivity.class);
        MainActivity.this.startActivity(myIntent);
    }

    /*@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        NearbyConnectionsController nearbyConnectionsController = new NearbyConnectionsController(getPackageManager(), getPackageName(), this);
        nearbyConnectionsController.clickedDrawerFragment(menuItem);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        switch (menuItem.getItemId()) {

            case R.id.nav_advertise:
            case R.id.nav_discover: {
                //drawer.closeDrawer(GravityCompat.START);
                //Toast.makeText(instance, "You may now close the drawer.", Toast.LENGTH_SHORT).show();
                //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                break;
            }
        }
        return true;
    }*/

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Since the permission was granted, we want to go straight back to the process we were starting before requesting the permission.
                    //TODO Trigger DataRetriever
                } else {
                    // permission denied
                    Toast.makeText(MainActivity.this, "Permission denied to read your external storage", Toast.LENGTH_SHORT).show();
                    //closeNow();
                }
            }
            break;
            case 1: {
                NearbyConnectionsController nearbyConnectionsController = new NearbyConnectionsController();
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (nearbyConnectionsController.getIsAdvertising()) {
                        nearbyConnectionsController.advertise();
                    } else if (nearbyConnectionsController.getIsDiscovering()) {
                        nearbyConnectionsController.discover();
                    }
                } else {  // permission denied
                    nearbyConnectionsController.setIsAdvertising(false);
                    nearbyConnectionsController.setIsDiscovering(false);
                    Toast.makeText(MainActivity.this, "Permission denied to access your device's location.", Toast.LENGTH_SHORT).show();
                }
            }
            break;
           /* case 2: {
                NearbyConnectionsController nearbyConnectionsController = new NearbyConnectionsController();
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Searching for devices.", Toast.LENGTH_SHORT).show();
                    nearbyConnectionsController.discover();
                }
                else {  // permission denied
                    nearbyConnectionsController.setIsDiscovering(false);
                    Toast.makeText(MainActivity.this, "Permission denied to access your device's location.", Toast.LENGTH_SHORT).show();
                }
            }*/
        }
    }
}