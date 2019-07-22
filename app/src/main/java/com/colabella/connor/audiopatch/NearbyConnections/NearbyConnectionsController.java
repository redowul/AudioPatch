package com.colabella.connor.audiopatch.NearbyConnections;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.widget.Toast;

import com.colabella.connor.audiopatch.MainActivity;

public class NearbyConnectionsController {

    private NavigationView navigationView;
    private PackageManager packageManager;
    private String packageName;
    private Context context;
    private MenuItem savedItem;
    private boolean isAdvertising = false;
    private boolean isDiscovering = false;

    public NearbyConnectionsController(NavigationView navigationView, PackageManager packageManager, String packageName, Context context){
        this.navigationView = navigationView;
        this.packageManager = packageManager;
        this.packageName = packageName;
        this.context = context;
    }

    public void clickedDrawerFragment(@NonNull MenuItem menuItem) {
        //Local PackageManager variable


        // set item as selected to persist highlight if it isn't advertise or discover
        //if (!menuItem.isChecked() && !menuItem.toString().equals("Advertise") && !menuItem.toString().equals("Discover")) {
            menuItem.setChecked(true);

        MainActivity mainActivity = new MainActivity();
        /*String colorPrimary = "#" + Integer.toHexString(ContextCompat.getColor(mainActivity.getStaticApplicationContext(), R.color.colorPrimary) & 0x00ffffff);
        String textColor = "#" + Integer.toHexString(ContextCompat.getColor(mainActivity.getStaticApplicationContext(), R.color.textColor) & 0x00ffffff);
        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] {-android.R.attr.state_checked}, // unchecked
                        new int[] { android.R.attr.state_checked}  // checked
                },
                new int[] {
                        Color.parseColor(textColor),
                        Color.parseColor(colorPrimary)
                }
        );
        if(menuItem.toString().equals("Advertise")) {
            navigationView.setItemTextColor(colorStateList);
        }*/

        //navigationView.setItemIconTintList(csl);
            //navigationView.setItemIconTintList(ColorStateList2);
      //  }

        // If permission has been granted, enable advertising/discovery with NearbyConnections
        /*else*/ /*if (!menuItem.isChecked() && menuItem.toString().equals("Advertise")
                && packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, packageName) == PackageManager.PERMISSION_GRANTED
                || !menuItem.isChecked() && menuItem.toString().equals("Discover")
                && packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, packageName) == PackageManager.PERMISSION_GRANTED) {
            //else if (!menuItem.isChecked() && menuItem.toString().equals("Advertise") || !menuItem.isChecked() && menuItem.toString().equals("Discover")) {
            if (savedItem != null) {
                Menu menu = navigationView.getMenu();
                for (int i = 0; i < menu.size(); i++) { //TODO is loop unnecessary?
                    //If Advertise checked = true when Discover is pressed, uncheck Advertise.
                    if (!menuItem.isChecked() && menuItem.toString().equals("Discover") &&
                            savedItem.isChecked() && savedItem.toString().equals("Advertise")) {

                        savedItem.setChecked(false);
                        menuItem.setChecked(true);

                        advertise();
                        discover();
                        //TODO Stop advertising, start discovering (But disband current room first.)
                      //  break;
                    }

                    //If Discover is checked when Advertise is pressed, uncheck Discover.
                    else if (!menuItem.isChecked() && menuItem.toString().equals("Advertise") &&
                            savedItem.isChecked() && savedItem.toString().equals("Discover")) {

                        savedItem.setChecked(false);
                        menuItem.setChecked(true);

                        discover();
                        advertise();
                        //TODO Stop discovering, start advertising (But leave current room first.)
                       // break;
                    }

                    //Advertise is unchecked and savedItem won't return a null error. Enable advertising
                    else if (!menuItem.isChecked() && menuItem.toString().equals("Advertise") &&
                            !savedItem.isChecked() && savedItem.toString().equals("Discover")) {
                        advertise();
                        menuItem.setChecked(true);
                        //break;
                    }

                    //Discover is unchecked and savedItem won't return a null error. Enable discovery
                    else if (!menuItem.isChecked() && menuItem.toString().equals("Discover") &&
                            !savedItem.isChecked() && savedItem.toString().equals("Advertise")) {
                        discover();
                        menuItem.setChecked(true);
                        //break;
                    }

                    else {
                        if (menuItem.toString().equals("Advertise")) {
                            advertise();
                            menuItem.setChecked(true);
                        }

                        else if (menuItem.toString().equals("Discover")) {
                            discover();
                            menuItem.setChecked(true);
                        }
                       // break;
                    }
                }
            }
            else { // savedItem is null and neither function is active, so fire whichever is pressed first.
                if (menuItem.toString().equals("Advertise")) {
                    advertise();
                    menuItem.setChecked(true);
                }

                else if (menuItem.toString().equals("Discover")) {
                    discover();
                    menuItem.setChecked(true);
                }
            }
        }

        else {
            //Functions to request device location and then enable functionality immediately after user approval
            if (!menuItem.isChecked() && menuItem.toString().equals("Advertise") && packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, packageName) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                this.isAdvertising = true;
            }
            else if (!menuItem.isChecked() && menuItem.toString().equals("Discover") && packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, packageName) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                this.isDiscovering = true;
            }

            //Disable functions if they're currently enabled and are being pressed again
            if (menuItem.isChecked() && menuItem.toString().equals("Advertise")) {
                advertise();
                menuItem.setChecked(false);
            }
            else if (menuItem.isChecked() && menuItem.toString().equals("Discover")) {
                discover();
                menuItem.setChecked(false);
            }
        }
        savedItem = menuItem;
        // close drawer when item is tapped
        //drawer.closeDrawers();

        //TODO Add code here to update the UI based on the item selected
        // For example, swap UI fragments here
        */
    }

    public boolean checkIsAdvertising() { return isAdvertising; }

    private void setIsAdvertising(boolean b) { this.isAdvertising = b; }

    public void advertise () {
        //Check the PackageManager to see if permissions have been enabled yet.

        //NearbyConnections nearbyConnections = new NearbyConnections();

        //If permission has been granted, start the activity.
        if (packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, packageName) == PackageManager.PERMISSION_GRANTED) {
            if (!this.isAdvertising) {
                this.isAdvertising = true;

                //n.setUserNickname(nickName);
                //nearbyConnections.startAdvertising(context);
                Toast.makeText(context, "Now advertising.", Toast.LENGTH_SHORT).show();
            }
            else {
                this.isAdvertising = false;
               // nearbyConnections.stopAdvertising();
                Toast.makeText(context, "No longer advertising.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            //this.isAdvertising = true;
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
    }

    public boolean checkIsDiscovering(){ return isDiscovering; }

    public void setIsDiscovering(boolean b){ isDiscovering = b; }

    public void discover () {
        //Check the PackageManager to see if permissions have been enabled yet.

        //NearbyConnections nearbyConnections = new NearbyConnections();

        //If permission has been granted, start the activity.
        if (packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, packageName) == PackageManager.PERMISSION_GRANTED) {
            if (!isDiscovering) {
                this.isDiscovering = true;

                //nearbyConnections.setUserNickname(nickName);
                //nearbyConnections.startDiscovery(context);
                Toast.makeText(context, "Searching for devices.", Toast.LENGTH_SHORT).show();

            }
            else {
                this.isDiscovering = false;
                //nearbyConnections.stopDiscovery();
                Toast.makeText(context, "Device search halted.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            //this.isDiscovering = true;
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
    }
}
