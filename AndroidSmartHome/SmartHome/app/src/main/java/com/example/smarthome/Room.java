package com.example.smarthome;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Room implements Parcelable {
    private String id;
    private String name;
    private int floor;
    private ArrayList<Light> lights;

    public Room(String name, int floor) {
        this.name = name;
        this.floor = floor;
        this.lights=new ArrayList<>();
    }
    public Room(String id,String name, int floor) {
        this.id=id;
        this.name = name;
        this.floor = floor;
        this.lights=new ArrayList<>();
    }

    @NonNull
    @Override
    public String toString() {
        return "Id: "+id+", Lights: "+lights+"\n";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFloor() {
        return floor;
    }

    public ArrayList<Light> getLights() {
        return lights;
    }

    public void addLight(Light light) {
        this.lights.add(light);
    }

    private Room(Parcel in)
    {
        id=in.readString();
        name=in.readString();
        floor=in.readInt();
        lights = (ArrayList<Light>) in.readSerializable();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeInt(floor);
        out.writeSerializable(lights);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        public Room[] newArray(int size) {
            return new Room[size];
        }
    };
}
