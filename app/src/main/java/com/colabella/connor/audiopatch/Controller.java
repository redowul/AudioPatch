package com.colabella.connor.audiopatch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.colabella.connor.audiopatch.NearbyConnections.User;

import java.util.List;

public class Controller {
    private static boolean[] permissions = {true}; // [0] RecyclerView Interactivity
    private static User user = new User(1 ,false, "TestName", permissions);

    public User getUser(){
        return user;
    }

    public void setUser(User userData){
        user = userData;
    }
}
