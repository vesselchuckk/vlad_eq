package ru.vlad.equipment_maintenance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "vlad.app.secret=9a4f2c8d3e1b7f6a5c4d3e2b1a0f9e8d7c6b5a4f3e2d1c0b9a8f7e6d5c4b3a2f",
    "vlad.app.lifetime=86400000"
})
class EquipmentMaintenanceApplicationTests {

    @Test
    void contextLoads() {
    }

}