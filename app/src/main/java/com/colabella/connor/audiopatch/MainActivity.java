package com.colabella.connor.audiopatch;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.Audio.AudioSingleton;
import com.colabella.connor.audiopatch.NearbyConnections.NearbyConnectionsController;
import com.colabella.connor.audiopatch.RecyclerView.ActivePlaylistAdapter;
import com.colabella.connor.audiopatch.RecyclerView.ActivePlaylistController;
import com.colabella.connor.audiopatch.RecyclerView.SwipeAndDragHelper;
import com.qhutch.bottomsheetlayout.BottomSheetLayout;
import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static MainActivity instance;

    public MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        Toolbar toolbar = findViewById(R.id.toolbar_top);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // drawer.openDrawer(GravityCompat.START); //TODO open and lock the drawer on boot to force the user to select advertise or discover
        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initializeRecyclerView();
        initializeBottomSheet();

        //TODO place this automatic loader inside an 'if' that triggers only when permissions authorize it

        // Ensures the data from the device storage is only retrieved once, when the list that data is stored in is empty
        if (AudioSingleton.getInstance().getAudioList() != null) {
            if (AudioSingleton.getInstance().getAudioList().size() == 0) {
                AudioController audioController = new AudioController();
                audioController.getAudioFilesFromDeviceStorage();
            }
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        String colorPrimary = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary) & 0x00ffffff);
        String textColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.textColor) & 0x00ffffff);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}  // checked
                },
                new int[]{
                        Color.parseColor(textColor),
                        Color.parseColor(colorPrimary)
                });
        navigationView.setItemTextColor(colorStateList);
        navigationView.setItemIconTintList(colorStateList);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (AudioSingleton.getInstance().getActivePlaylistAdapter().getItemCount() > 0) {
            Audio selectedItem = AudioSingleton.getInstance().getActivePlaylistAdapter().getSelectedAudio();

            ActivePlaylistController activePlaylistController = new ActivePlaylistController();
            activePlaylistController.alterBottomSheet(selectedItem);
            activePlaylistController.togglePlayButtonState();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

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
        ActivePlaylistController activePlaylistController = new ActivePlaylistController();

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.audiopatchlogosquareblurrable);
        icon = activePlaylistController.blur(this, icon); // blur the image

        // Set background of the bottom sheet
        ImageView bottomSheetAlbumCover = findViewById(R.id.bottom_sheet_album_cover);
        bottomSheetAlbumCover.setImageBitmap(icon);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenW = displayMetrics.widthPixels;
        BottomSheetLayout layout = findViewById(R.id.bottom_sheet_layout);
        layout.getLayoutParams().height = (int) (screenW * .75); // set height of the bottom sheet to 75% the width of the screen

        if (!layout.isExpended()) {
            Button expandBottomSheetButton = findViewById(R.id.expand_bottom_sheet_button);
            expandBottomSheetButton.setOnClickListener(view -> {
                layout.expand();
            });
        }

        layout.setOnProgressListener(progress -> {

            AppBarLayout bottomSheetLayoutCapstone = findViewById(R.id.bottom_sheet_layout_capstone);
            double minY = layout.getBottom();
            double maxY = layout.getTop();
            double currentY = layout.getY();

            double adjustedMinY = bottomSheetLayoutCapstone.getHeight();
            double adjustedMaxY = maxY - minY;
            double currentAdjustedY = maxY - currentY;

            double percentage = (currentAdjustedY / adjustedMaxY) * 100;
            int alpha = (int) ((percentage / 100) * 255);

            ImageView bottomSheetLayoutCapstoneAlbumCover = findViewById(R.id.bottom_sheet_current_album_cover_small);
            Button expandBottomSheetButton = findViewById(R.id.expand_bottom_sheet_button);
            bottomSheetLayoutCapstone.getBackground().setAlpha(alpha); // set the alpha of the appbar itself
            expandBottomSheetButton.getBackground().setAlpha(alpha);

            TextView bottomSheetCapstoneTitle = findViewById(R.id.bottom_sheet_capstone_title);
            TextView bottomSheetCapstoneArtist = findViewById(R.id.bottom_sheet_capstone_artist);

            if (bottomSheetCapstoneTitle.getVisibility() == View.VISIBLE && bottomSheetCapstoneArtist.getVisibility() == View.VISIBLE) {
                bottomSheetCapstoneTitle.setTextColor(Color.argb(alpha, 255, 255, 255));
                bottomSheetCapstoneArtist.setTextColor(Color.argb(alpha, 255, 255, 255));
            }

            if (bottomSheetLayoutCapstoneAlbumCover.getDrawable() != null) {
                bottomSheetLayoutCapstoneAlbumCover.getDrawable().setAlpha(alpha); // set the alpha of the bitmap overlain on top of the image view
            }

            if (adjustedMinY == minY - currentY) {
                if (bottomSheetLayoutCapstoneAlbumCover.getDrawable() != null) {
                    bottomSheetLayoutCapstoneAlbumCover.getDrawable().setAlpha(255); // set the alpha of the bitmap overlain on top of the image view
                    expandBottomSheetButton.getBackground().setAlpha(255); // set the expand bottom sheet button to be completely visible
                }
                bottomSheetLayoutCapstone.getBackground().setAlpha(255);

                ActivePlaylistAdapter activePlaylistAdapter = new ActivePlaylistAdapter();
                if (activePlaylistAdapter.getItemCount() == 0) {
                    Bitmap blurredAlbumCover = BitmapFactory.decodeResource(getInstance().getResources(), R.drawable.audiopatchlogosquareblurrable); // getting the resource, it isn't blurred yet
                    blurredAlbumCover = activePlaylistController.blur(getInstance(), blurredAlbumCover); // blur the image
                    bottomSheetAlbumCover.setImageBitmap(blurredAlbumCover); // Set background of the bottom sheet capstone image; this one isn't blurred yet
                }

                /*if(activePlaylistAdapter.getItemCount() == 0) {
                    layout.setVisibility(View.GONE);
                }
                else {
                    layout.setVisibility(View.VISIBLE);
                }*/
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.options_clear_queue) {
          /*  RecyclerViewController recyclerViewController = new RecyclerViewController();
            recyclerViewController.clearAudioList();
            Button playButton = findViewById(R.id.play_button);
            playButton.setBackgroundResource(R.drawable.ic_play_24dp);
            */
            return false;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        NearbyConnectionsController nearbyConnectionsController = new NearbyConnectionsController(getPackageManager(), getPackageName(), this);
        nearbyConnectionsController.clickedDrawerFragment(menuItem);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        switch (menuItem.getItemId()) {

            case R.id.nav_advertise:
            case R.id.nav_discover: {
                //drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(instance, "You may now close the drawer.", Toast.LENGTH_SHORT).show();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                break;
            }
        }
        return true;
    }

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