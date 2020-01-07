package com.example.smarthome;


import java.util.ArrayList;

public interface RoomContextStateListener {
    void updateView(ArrayList<Room> rooms);
    void updateView(Room rooms);
    void updateLight(Light light);
}
