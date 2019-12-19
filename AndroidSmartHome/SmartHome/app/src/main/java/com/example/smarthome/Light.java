package com.example.smarthome;

import androidx.annotation.NonNull;

public class Light {
    private String id;
    private int level;
    private boolean on;
    private int color;
    private Room room;

    public Light(String id, Room room) {
        this.id = id;
        this.room = room;
        this.level=1;
        this.on=false;
    }

    public Light(String id, int level, boolean on, int color,Room room) {
        this.id = id;
        this.level=level;
        this.on=on;
        this.color=color;
        this.room = room;

    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public boolean isOn() {
        return on;
    }

    public int getColor()
    {
        return color;
    }
    public Room getRoom() {
        return room;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @NonNull
    @Override
    public String toString() {
        return "Id: "+id+", Level: "+level+", On: "+on+"\n";
    }
}
