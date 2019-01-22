package com.colabella.connor.audiopatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.widget.Toast;

import com.colabella.connor.audiopatch.Audio.AudioController;
import com.colabella.connor.audiopatch.RecyclerView.RecyclerViewAdapter;
import com.colabella.connor.audiopatch.RecyclerView.RecyclerViewController;
import com.colabella.connor.audiopatch.RecyclerView.SwipeAndDragHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static Context applicationContext;
    private static Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeRecyclerView();

        applicationContext = getApplicationContext();
        playButton = findViewById(R.id.play_button);
    }

    private void initializeRecyclerView(){
        AudioController audioController = new AudioController();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter recyclerViewAdapter = audioController.getRecyclerViewAdapter();

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
            RecyclerViewController recyclerViewController = new RecyclerViewController();
            recyclerViewController.clearAudioList();
            playButton.setBackgroundResource(R.drawable.ic_play_24dp);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    public void onBottomToolbarItemClick(View view){
        RecyclerViewController recyclerViewController = new RecyclerViewController();
        recyclerViewController.determineButtonSelected(getResources().getResourceEntryName(view.getId()), view); // Passes determineButtonSelected() the String ID of the pressed button.
    }

    //Enables audio selection
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void selectAudioFromStorage(View view) {
        AudioController audioController = new AudioController();
        PackageManager packageManager = getPackageManager(); //PackageManager checks to see if permissions have been enabled yet.
        Activity activity = this;

        audioController.selectAudioFromStorage(packageManager, activity); // Fires intent to search the device storage for audio to add to the RecyclerView
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        AudioController audioController = new AudioController();
        PackageManager packageManager = getPackageManager(); // PackageManager checks to see if permissions have been enabled yet.
        Activity activity = this;
        Context context = this;

        audioController.onRequestPermissionsResult(requestCode, permissions, grantResults, packageManager, activity, context);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if(resultData != null) {
            if(resultCode == RESULT_OK && requestCode == 0){
                Toast.makeText(this, "Audio selected.", Toast.LENGTH_SHORT).show();
                Context context = this;
                AudioController audioController = new AudioController();
                audioController.onActivityResult(resultData, context);
            }
        }
    }

    public Context getStaticApplicationContext(){ return applicationContext; }

    public void togglePlayButtonState(){ //TODO re-write when less tired (Needed for any action that isn't a button to toggle the state of the PlayButton)
        AudioController audioController = new AudioController();
        MediaPlayer mediaPlayer = audioController.getMediaPlayer();
        playButton.setBackgroundResource(R.drawable.ic_pause_24dp);
        if(mediaPlayer == null){ playButton.setBackgroundResource(R.drawable.ic_play_24dp); }
    }
}