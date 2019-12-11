package com.emse.spring.faircorp.dto;

import com.emse.spring.faircorp.model.light.Light;
import com.emse.spring.faircorp.model.room.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomDTO {
    private Long id;
    private String name;
    private int floor;
    private List<LightDTO> lights;

    public RoomDTO() {
    }
    public RoomDTO(Room room) {
        this.id = room.getId();
        this.name = room.getName();
        this.floor = room.getFloor();
        this.lights = new ArrayList<>();
        for(Light light:room.getLights())
            this.lights.add(new LightDTO(light));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFloor() {
        return floor;
    }

    public List<LightDTO> getLights() {
        return lights;
    }
}
