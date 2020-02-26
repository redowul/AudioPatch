package com.colabella.connor.audiopatch.controllers;

import com.colabella.connor.audiopatch.nearbyconnections.User;

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
