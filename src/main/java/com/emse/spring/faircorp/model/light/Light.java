package com.emse.spring.faircorp.model.light;

import com.emse.spring.faircorp.model.room.Room;
import com.emse.spring.faircorp.model.Status;

import javax.persistence.*;

@Entity
public class Light {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer color;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;


    @ManyToOne
    private Room room;

    public Light() {

    }

    public Light(Integer level,Integer color, Status status,Room room) {
        this.level = level;
        this.status = status;
        this.color = color;
        this.room = room;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Room getRoom() {
        return room;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}
