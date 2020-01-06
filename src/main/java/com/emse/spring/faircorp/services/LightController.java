package com.emse.spring.faircorp.services;

import com.emse.spring.faircorp.dto.LightDTO;
import com.emse.spring.faircorp.model.Status;
import com.emse.spring.faircorp.model.light.Light;
import com.emse.spring.faircorp.model.light.LightDao;
import com.emse.spring.faircorp.model.room.RoomDao;
import com.emse.spring.faircorp.mqtt.MqttController;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/lights")
@Transactional
public class LightController {

    @Autowired
    private LightDao lightDao;
    @Autowired
    private RoomDao roomDao;

    @Autowired
    private MqttController mqttController;

    @GetMapping // 5.
    public List<LightDTO> findAll() {
        return lightDao.findAll()
                .stream()
                .map(LightDTO::new)
                .collect(Collectors.toList());
    }
    @GetMapping(path ="/on")
    public List<LightDTO> findOnLights(){
        return lightDao.findOnLights().stream().map(LightDTO::new).collect(Collectors.toList());
    }
    @GetMapping(path = "/{id}")
    public LightDTO findById(@PathVariable Long id) {
        return lightDao.findById(id).map(light -> new LightDTO(light)).orElse(null);
    }

    @PutMapping(path = "/{id}/switch")
    public LightDTO switchStatus(@PathVariable Long id) {
        Light light = lightDao.findById(id).orElseThrow(IllegalArgumentException::new);
        light.setStatus(light.getStatus() == Status.ON ? Status.OFF : Status.ON);
        String message=createMqttMessage(light);
        mqttController.publish("a_a_m_light",message);
        return new LightDTO(light);
    }

    @PostMapping
    public LightDTO create(@RequestBody LightDTO dto) {
        Light light = null;
        if (dto.getId() != null) {
            light = lightDao.findById(dto.getId()).orElse(null);
        }

        if (light == null) {
            light = lightDao.save(new Light(dto.getLevel(),dto.getColor(), dto.getStatus(), roomDao.getOne(dto.getRoomId())));
        } else {
            light.setLevel(dto.getLevel());
            light.setStatus(dto.getStatus());
            lightDao.save(light);
        }
        return new LightDTO(light);
    }
    @PostMapping(path = "/{id}/bri")
    public LightDTO changeBri(@PathVariable Long id,@RequestBody LightDTO lightDTO){
        int level = lightDTO.getLevel();
        Light light = lightDao.findById(id).orElseThrow(IllegalArgumentException::new);
        light.setLevel(level);
        String message=createMqttMessage(light);
        mqttController.publish("a_a_m_light",message);
        return new LightDTO(light);
    }

    @PostMapping(path = "/{id}/hue")
    public LightDTO changeHue(@PathVariable Long id,@RequestBody LightDTO lightDTO){
        int color = lightDTO.getColor();
        Light light = lightDao.findById(id).orElseThrow(IllegalArgumentException::new);
        light.setColor(color);
        String message=createMqttMessage(light);
        mqttController.publish("a_a_m_light",message);
        return new LightDTO(light);
    }
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        lightDao.deleteById(id);
    }

    private String createMqttMessage (Light light)
    {
        JSONObject object=new JSONObject();
        object.put("id",light.getId());
        object.put("room",light.getRoom());
        JSONObject properties=new JSONObject();

        properties.put("on",true);
        if(light.getStatus().equals(Status.OFF))
            properties.put("on",false);
        properties.put("bri",light.getLevel());
        properties.put("hue",light.getColor());

        object.put("properties",properties);
        System.out.println(object.toString());
        return object.toString();
    }
}
