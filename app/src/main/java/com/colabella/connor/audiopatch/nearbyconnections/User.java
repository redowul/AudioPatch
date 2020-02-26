package com.colabella.connor.audiopatch.nearbyconnections;

public class User {

    private int id;
    private boolean isHost;
    private String displayName;
    private boolean[] permissions;  // [0] RecyclerView Interactivity

    public User(int id, boolean isHost, String displayName, boolean[] permissions) {
        this.id = id;
        this.isHost = isHost;
        this.displayName = displayName;
        this.permissions = permissions;
    }

    public int getUserId() {
        return id;
    }

    public boolean getIsHost() {
        return isHost;
    }

    public void setIsGuest(boolean guestStatus) {
        isHost = guestStatus;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean getRecyclerViewPermission(){
        return this.permissions[0];
    }

    public void toggleRecyclerViewPermission(){ // Toggles User RecyclerView interactivity permission
        this.permissions[0] = !this.permissions[0];
    }
}
