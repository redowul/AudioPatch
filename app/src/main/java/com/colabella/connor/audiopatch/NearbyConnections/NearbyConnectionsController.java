package com.colabella.connor.audiopatch.NearbyConnections;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;

public class NearbyConnectionsController {

    private NavigationView navigationView;
    private PackageManager packageManager;
    private String packageName;
    private Context context;
    private static MenuItem savedItem;
    private static boolean isAdvertising = false;
    private static boolean isDiscovering = false;

    public NearbyConnectionsController(){ }

    public NearbyConnectionsController(PackageManager packageManager, String packageName, Context context){
        //this.navigationView = navigationView;
        this.packageManager = packageManager;
        this.packageName = packageName;
        this.context = context;
    }

    public boolean getIsAdvertising(){ return isAdvertising; }

    public void setIsAdvertising(boolean b) { isAdvertising = b; }

    public boolean getIsDiscovering(){ return isDiscovering; }

    public void setIsDiscovering(boolean b){ isDiscovering = b; }

    public void clickedDrawerFragment(@NonNull MenuItem menuItem) { // function to handle enabling of advertising/discovery with NearbyConnections
        if (packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, packageName) == PackageManager.PERMISSION_GRANTED) {
            //if (menuItem.toString().equals("Advertise") || menuItem.toString().equals("Discover")) {
               if(!menuItem.isChecked()) {
                   if(menuItem.toString().equals("Advertise")) {

                   }
                   menuItem.setChecked(true);
               }
               else {
                   menuItem.setChecked(false);
               }
        }
        else {

            if (!menuItem.isChecked() && menuItem.toString().equals("Advertise")) { isAdvertising = true; }
            else { isDiscovering = true; }
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        savedItem = menuItem;

    }
            //else if (!menuItem.isChecked() && menuItem.toString().equals("Advertise") || !menuItem.isChecked() && menuItem.toString().equals("Discover")) {
     /*       if (savedItem != null) {
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


        savedItem = menuItem;
        // close drawer when item is tapped
        //drawer.closeDrawers();

        //TODO Add code here to update the UI based on the item selected
        // For example, swap UI fragments here
    }*/

    /*public void clickedDrawerFragment(@NonNull MenuItem menuItem) {
        //Local PackageManager variable


        // set item as selected to persist highlight if it isn't advertise or discover
        //if (!menuItem.isChecked() && !menuItem.toString().equals("Advertise") && !menuItem.toString().equals("Discover")) {
           // menuItem.setChecked(true);

       // if(menuItem.toString().equals("Advertise")) {
        //    navigationView.setItemTextColor(colorStateList);
        //    navigationView.setItemIconTintList(colorStateList);
       // }

        //navigationView.setItemIconTintList(csl);
            //navigationView.setItemIconTintList(ColorStateList2);
      //  }

        // If permission has been granted, enable advertising/discovery with NearbyConnections
        if (!menuItem.isChecked() && menuItem.toString().equals("Advertise")
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
                isAdvertising = true;
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            else if (!menuItem.isChecked() && menuItem.toString().equals("Discover") && packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, packageName) != PackageManager.PERMISSION_GRANTED) {
                isDiscovering = true;
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
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
    }*/

    public void advertise () {
        //Check the PackageManager to see if permissions have been enabled yet.

        //NearbyConnections nearbyConnections = new NearbyConnections();
        //If permission has been granted, start the activity.
        //if (packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, packageName) == PackageManager.PERMISSION_GRANTED) {
            if (!isAdvertising) {
                isAdvertising = true;

                MainActivity mainActivity = new MainActivity();
                Toast.makeText(mainActivity.getInstance(), "Now advertising.", Toast.LENGTH_SHORT).show();
                //n.setUserNickname(nickName);
                //nearbyConnections.startAdvertising(context);
            }
            else {
                isAdvertising = false;
               // nearbyConnections.stopAdvertising();
                MainActivity mainActivity = new MainActivity();
                Toast.makeText(mainActivity.getInstance(), "No longer advertising.", Toast.LENGTH_SHORT).show();
            }
       // }
        //else {
            //this.isAdvertising = true;
        //    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
      //  }
    }

    public void discover () {
        //Check the PackageManager to see if permissions have been enabled yet.
        //NearbyConnections nearbyConnections = new NearbyConnections();
        //If permission has been granted, start the activity.
        if (packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, packageName) == PackageManager.PERMISSION_GRANTED) {
            if (!isDiscovering) {
                isDiscovering = true;

                //nearbyConnections.setUserNickname(nickName);
                //nearbyConnections.startDiscovery(context);
                MainActivity mainActivity = new MainActivity();
                Toast.makeText(mainActivity.getInstance(), "Searching for devices.", Toast.LENGTH_SHORT).show();
            }
            else {
                isDiscovering = false;
                //nearbyConnections.stopDiscovery();
                Toast.makeText(context, "Device search halted.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            //this.isDiscovering = true;
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }
}