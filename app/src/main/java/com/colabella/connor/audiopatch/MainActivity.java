package com.colabella.connor.audiopatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import android.widget.TextView;
import android.widget.Toast;

import com.colabella.connor.audiopatch.NearbyConnections.NearbyConnectionsController;
import com.colabella.connor.audiopatch.RecyclerView.ActivePlaylistAdapter;
import com.colabella.connor.audiopatch.RecyclerView.ActivePlaylistController;
import com.colabella.connor.audiopatch.RecyclerView.SwipeAndDragHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static Context applicationContext;
    @SuppressLint("StaticFieldLeak")
    private static Button playButton;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

       initializeRecyclerView();

       applicationContext = getApplicationContext();
       playButton = findViewById(R.id.play_button);

       navigationView = findViewById(R.id.nav_view);
       navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeRecyclerView(){
        ActivePlaylistController activePlaylistController = new ActivePlaylistController();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ActivePlaylistAdapter recyclerViewAdapter = activePlaylistController.getActivePlaylistAdapter();

        SwipeAndDragHelper swipeAndDragHelper = new SwipeAndDragHelper(recyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAndDragHelper);
        recyclerViewAdapter.setTouchHelper(itemTouchHelper);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO Move this method to Controller.
    public void toggleBottomToolbarVisibility(View view) {
        Toolbar toolbarBottom = findViewById(R.id.toolbar_bottom);
        if(toolbarBottom.getVisibility() == View.VISIBLE) {
            toolbarBottom.setVisibility(View.GONE);
        }
        else {
            toolbarBottom.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        NearbyConnectionsController nearbyConnectionsController = new NearbyConnectionsController(navigationView, getPackageManager(), getPackageName(), MainActivity.this);
        nearbyConnectionsController.clickedDrawerFragment(menuItem);
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

    // Return static variables
    public Context getStaticApplicationContext(){ return applicationContext; }
    public Button getPlayButton(){ return playButton; }
}