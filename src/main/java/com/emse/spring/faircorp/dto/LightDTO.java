package com.emse.spring.faircorp.dto;

import com.emse.spring.faircorp.model.Status;
import com.emse.spring.faircorp.model.light.Light;
import com.emse.spring.faircorp.model.room.Room;
import org.springframework.web.bind.annotation.CrossOrigin;


public class LightDTO {
    private  Long id;
    private  Integer level;
    private Status status;
    private Long roomId;
    public LightDTO() {
    }

    public LightDTO(Light light) {
        this.id = light.getId();
        this.level = light.getLevel();
        this.status = light.getStatus();
        this.roomId = light.getRoom().getId();
    }

    public Long getId() {
        return id;
    }

    public Integer getLevel() {
        return level;
    }

    public Status getStatus() {
        return status;
    }
    public Long getRoomId(){
        return this.roomId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
