package com.colabella.connor.audiopatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
    @SuppressLint("StaticFieldLeak")
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
            Button playButton = findViewById(R.id.play_button);
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

        // When the add audio button is pressed, it can sometimes try to open the storage search menu more than once, requiring us to back out of tw instances
        // of that same menu if we don't actually want to send anything. This prevents that by disabling the add button for 1 second whenever it's pressed.
        Button button = findViewById(R.id.add_audio_button2); //TODO fix button name (relative to the example one on the lower toolbar)
        button.setEnabled(false);   // Disable the button
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Button button = findViewById(R.id.add_audio_button2); //TODO fix button name (relative to the example one on the lower toolbar)
                button.setEnabled(true);    // Re-enable the button
            }
        }, 1000);   // Delay for 1 second

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

    // Return static variables
    public Context getStaticApplicationContext(){ return applicationContext; }
    public Button getPlayButton(){ return playButton; }
}