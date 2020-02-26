package com.colabella.connor.audiopatch;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.controllers.AudioController;
import com.colabella.connor.audiopatch.controllers.SingletonController;
import com.colabella.connor.audiopatch.controllers.BottomSheetController;
import com.colabella.connor.audiopatch.fragments.GuestFragment;
import com.colabella.connor.audiopatch.recyclerview.ActivePlaylistAdapter;
import com.colabella.connor.audiopatch.controllers.ActivePlaylistController;
import com.colabella.connor.audiopatch.recyclerview.MainDrawerAdapter;
import com.colabella.connor.audiopatch.recyclerview.MainDrawerSecondaryAdapter;
import com.colabella.connor.audiopatch.recyclerview.SwipeAndDragHelper;
import com.qhutch.bottomsheetlayout.BottomSheetLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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

        BottomSheetController bottomSheetController = new BottomSheetController();
        ActivePlaylistController activePlaylistController = new ActivePlaylistController();

        initializeRecyclerView();
        initializeBottomSheet();
        bottomSheetController.initializeBottomSeekbar(this);

        ImageView header = findViewById(R.id.drawer_header);
        header.setImageResource(R.drawable.audiopatch_header_transparent);
        initializeNavigationView();

        // Automatically retrieves audio files from storage if the application has already been granted permission to do so
        PackageManager packageManager = getPackageManager();
        if(packageManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            if (SingletonController.getInstance().getAudioList() != null) {
                if (SingletonController.getInstance().getAudioList().size() == 0) {
                    AudioController audioController = new AudioController();
                    audioController.getAudioFilesFromDeviceStorage(); // Retrieves audio files from storage
                }
            }
        }

        if (SingletonController.getInstance().getActivePlaylistAdapter().getItemCount() > 0) {
            Audio selectedItem = SingletonController.getInstance().getActivePlaylistAdapter().getSelectedAudio();

            bottomSheetController.alterBottomSheet(selectedItem);
            activePlaylistController.togglePlayButtonState();
        }

        // Handles headphone plugging and unplugging
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        AudioController.HeadphonesInUseReceiver headphonesInUseReceiver = new AudioController.HeadphonesInUseReceiver ();
        registerReceiver( headphonesInUseReceiver, receiverFilter );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(getSupportFragmentManager().getBackStackEntryCount() > 0 && !drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START); // Open the drawer
        }
        else if (drawer.isDrawerOpen(GravityCompat.START)) { // close drawer, set add audio button to be visible
            Button addAudioButton = findViewById(R.id. add_audio_button);
            addAudioButton.setVisibility(View.VISIBLE);

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            drawer.closeDrawer(GravityCompat.START);

            MainDrawerAdapter mainDrawerAdapter = SingletonController.getInstance().getMainDrawerAdapter();
            mainDrawerAdapter.setSelectedMenuItem(0);

            if(SingletonController.getInstance().isGuest()) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                GuestFragment guestFragment = new GuestFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, guestFragment, "GuestFragment").addToBackStack("open_guest").commit();
                addAudioButton.setVisibility(View.GONE);
            }
        }
        else { // Move the task containing this activity to the back of the activity stack.
            moveTaskToBack(true);
        }
    }

    /**
     * Initialization methods
     **/

    private void initializeNavigationView() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Button openDrawerButton = findViewById(R.id.open_drawer_button);
        openDrawerButton.setOnClickListener(view -> drawer.openDrawer(GravityCompat.START)); // close the sheet if pressed

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        /* DrawerLayout Primary RecyclerView  */

        RecyclerView primaryItemsRecyclerView = findViewById(R.id.drawer_recycler_view);
        primaryItemsRecyclerView.setLayoutManager(linearLayoutManager);

        MainDrawerAdapter mainDrawerAdapter = SingletonController.getInstance().getMainDrawerAdapter();
        String home = getResources().getString(R.string.home);
        String settings = getResources().getString(R.string.settings);
        //String about = getResources().getString(R.string.about);
        mainDrawerAdapter.addItem(home, true);
        mainDrawerAdapter.addItem(settings, false);
        //mainDrawerAdapter.addItem(about, false);
        primaryItemsRecyclerView.setAdapter(mainDrawerAdapter);

        TextView username = findViewById(R.id.username);
        String phoneModel = SingletonController.getInstance().getUsername();
        username.setText(phoneModel);

        /* DrawerLayout Secondary RecyclerView  */

        LinearLayoutManager secondaryLinearLayoutManager = new LinearLayoutManager(this);

        RecyclerView secondaryItemsRecyclerView = findViewById(R.id.drawer_secondary_recycler_view);
        secondaryItemsRecyclerView.setLayoutManager(secondaryLinearLayoutManager);

        MainDrawerSecondaryAdapter mainDrawerSecondaryAdapter = new MainDrawerSecondaryAdapter();
        String host = getResources().getString(R.string.host);
        String join = getResources().getString(R.string.join);
        mainDrawerSecondaryAdapter.addItem(host, false);
        mainDrawerSecondaryAdapter.addItem(join, false);

        secondaryItemsRecyclerView.setAdapter(mainDrawerSecondaryAdapter);


    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ActivePlaylistAdapter recyclerViewAdapter = SingletonController.getInstance().getActivePlaylistAdapter();

        SwipeAndDragHelper swipeAndDragHelper = new SwipeAndDragHelper(recyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAndDragHelper);
        recyclerViewAdapter.setTouchHelper(itemTouchHelper);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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

            SeekBar bottomSheetCapstoneSeekBar = findViewById(R.id.bottom_sheet_capstone_seekbar);
            bottomSheetCapstoneSeekBar.setAlpha(capstoneAlpha); // sets transparency of capstone seekbar

            // Sets the location of the seekbar on the screen.
            // Note that placement is handled manually here, and that the seekbar's view is NOT located inside the bottom sheet.
            SeekBar seekBar = findViewById(R.id.bottom_sheet_seekbar);
            TextView seekbarPosition = findViewById(R.id.seekbar_position);
            TextView audioLength = findViewById(R.id.audio_length);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // only for gingerbread and newer versions
                seekBar.setY((int) (currentY + (bottomSheetSize * .50)));
                seekbarPosition.setY((int) (currentY + (bottomSheetSize * .49)));
                audioLength.setY((int) (currentY + (bottomSheetSize * .49)));
            }
            else {
                seekBar.setY((int) (currentY + (bottomSheetSize * .70)));
                seekbarPosition.setY((int) (currentY + (bottomSheetSize * .69)));
                audioLength.setY((int) (currentY + (bottomSheetSize * .69)));
            }

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
                if (!isItemSelected) {
                    BottomSheetController bottomSheetController = new BottomSheetController();
                    bottomSheetController.resetBottomSheet();
                }
            }
        });
    }

    // Handles all button clicks that occur in context_main.xml.
    public void onBottomToolbarItemClick(View view) {
        ActivePlaylistController activePlaylistController = new ActivePlaylistController();
        activePlaylistController.determineButtonSelected(getResources().getResourceEntryName(view.getId()), view); // Passes determineButtonSelected() the String ID of the pressed button.
    }

    //Enables audio selection
    public void startDataRetrievalActivity(View view) {
        PackageManager packageManager = getPackageManager();
        if(packageManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            if (SingletonController.getInstance().getAudioList() != null) {
                if (SingletonController.getInstance().getAudioList().size() == 0) {
                     AudioController audioController = new AudioController();
                     audioController.getAudioFilesFromDeviceStorage(); // Retrieves audio files from storage
                }
            }
            Intent myIntent = new Intent(MainActivity.this, DataRetrievalActivity.class);
            MainActivity.this.startActivity(myIntent);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
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

    //TODO need to actually implement all permissions properly
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: { // device storage permission
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (SingletonController.getInstance().getAudioList() != null) {
                        if (SingletonController.getInstance().getAudioList().size() == 0) {
                            AudioController audioController = new AudioController();
                            audioController.getAudioFilesFromDeviceStorage(); // Retrieves audio files from storage
                        }
                    }
                    Intent myIntent = new Intent(this, DataRetrievalActivity.class);
                    this.startActivity(myIntent);
                } else {
                    // permission denied
                    Toast.makeText(this, "Permission to read your external storage was denied", Toast.LENGTH_SHORT).show();
                    //closeNow();
                }
            }
            break;
            case 1: {

            }
            break;
         /*   case 1: {
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