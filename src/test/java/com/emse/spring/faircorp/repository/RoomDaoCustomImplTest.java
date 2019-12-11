package com.emse.spring.faircorp.repository;

import com.emse.spring.faircorp.model.room.RoomDao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class RoomDaoCustomImplTest {
    @Autowired
    private RoomDao roomDao;

    @Test
    public void shouldFindOnLights() {
        Assertions.assertThat(roomDao.findRoomByName("Room3")).isNotNull();
        System.out.println("----------------------------------- worked ! -----------------------------------------");
    }
}
