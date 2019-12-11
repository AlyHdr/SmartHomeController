package com.emse.spring.faircorp.model.room;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class RoomDaoCustomImpl implements RoomDaoCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Room findRoomByName(String name) {
        String jpql = "select rm from Room rm where rm.name = :value";
        return em.createQuery(jpql, Room.class)
                .setParameter("value", name)
                .getSingleResult();
    }
}
