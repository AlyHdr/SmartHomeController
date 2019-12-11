package com.emse.spring.faircorp.model.light;

import com.emse.spring.faircorp.model.light.Light;

import java.util.List;

public interface LightDaoCustom {
    List<Light> findOnLights();
}
