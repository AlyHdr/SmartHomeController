package com.emse.spring.faircorp.model.room;

import com.emse.spring.faircorp.model.light.Light;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Room {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int floor;

    @OneToMany(mappedBy = "room")
    private List<Light> lights;

    public Room() {
    }

    public Room(String name, int floor) {
        this.name = name;
        this.floor = floor;
        lights = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public List<Light> getLights() {
        return lights;
    }

    public void setLights(List<Light> lights) {
        this.lights = lights;
    }
}
