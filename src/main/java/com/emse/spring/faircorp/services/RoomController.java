package com.emse.spring.faircorp.services;

import com.emse.spring.faircorp.dto.LightDTO;
import com.emse.spring.faircorp.dto.RoomDTO;
import com.emse.spring.faircorp.model.Status;
import com.emse.spring.faircorp.model.light.Light;
import com.emse.spring.faircorp.model.light.LightDao;
import com.emse.spring.faircorp.model.room.Room;
import com.emse.spring.faircorp.model.room.RoomDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
@Transactional
public class RoomController {
    @Autowired
    private LightDao lightDao;
    @Autowired
    private RoomDao roomDao;
    @GetMapping // 5.
    public List<RoomDTO> findAll() {
        return roomDao.findAll()
                .stream()
                .map(RoomDTO::new)
                .collect(Collectors.toList());
    }
    @PostMapping
    public RoomDTO create(@RequestBody RoomDTO dto) {
        Room room = null;
        if (dto.getId() != null) {
            room = roomDao.findById(dto.getId()).orElse(null);
        }

        if (room == null) {
            room = roomDao.save(new Room(dto.getName(),dto.getFloor()));
        } else {
            room.setName(dto.getName());
            room.setFloor(dto.getFloor());
            roomDao.save(room);
        }

        return new RoomDTO(room);
    }
    @GetMapping(path = "/{id}")
    public RoomDTO findById(@PathVariable Long id) {
        return roomDao.findById(id).map(room -> new RoomDTO(room)).orElse(null);
    }
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        RoomDTO roomDTO = findById(id);
        for(LightDTO light:roomDTO.getLights()){
            lightDao.deleteById(light.getId());
        }
        roomDao.deleteById(id);
    }
    @PutMapping(path = "/{id}/switch")
    public List<LightDTO> switchStatus(@PathVariable Long id) {
        RoomDTO roomDTO = findById(id);
        ArrayList<LightDTO> list_light_dto = new ArrayList<>();
        for(LightDTO lightDTO:roomDTO.getLights()){
            Light light = lightDao.findById(lightDTO.getId()).orElseThrow(IllegalArgumentException::new);

            light.setStatus(light.getStatus() == Status.ON ? Status.OFF : Status.ON);
            list_light_dto.add(new LightDTO(light));
        }

        return list_light_dto;
    }
}
