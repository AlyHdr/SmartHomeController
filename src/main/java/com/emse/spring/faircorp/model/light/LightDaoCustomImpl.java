package com.emse.spring.faircorp.model.light;

import com.emse.spring.faircorp.model.Status;
import com.emse.spring.faircorp.model.light.Light;
import com.emse.spring.faircorp.model.light.LightDaoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class LightDaoCustomImpl implements LightDaoCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Light> findOnLights() {

        String jpql = "select lt from Light lt where lt.status = :value";
        return em.createQuery(jpql, Light.class)
                .setParameter("value", Status.ON)
                .getResultList();
    }
}
