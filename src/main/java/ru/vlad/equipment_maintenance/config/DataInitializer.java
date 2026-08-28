package ru.vlad.equipment_maintenance.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.vlad.equipment_maintenance.entity.Equipment;
import ru.vlad.equipment_maintenance.entity.ServiceTeam;
import ru.vlad.equipment_maintenance.entity.SparePart;
import ru.vlad.equipment_maintenance.entity.FailureHistory;
import ru.vlad.equipment_maintenance.repository.EquipmentRepository;
import ru.vlad.equipment_maintenance.repository.ServiceTeamRepository;
import ru.vlad.equipment_maintenance.repository.SparePartRepository;
import ru.vlad.equipment_maintenance.repository.FailureHistoryRepository;

import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EquipmentRepository equipmentRepository;
    private final ServiceTeamRepository serviceTeamRepository;
    private final SparePartRepository sparePartRepository;
    private final FailureHistoryRepository failureHistoryRepository;

    public DataInitializer(EquipmentRepository equipmentRepository,
                           ServiceTeamRepository serviceTeamRepository,
                           SparePartRepository sparePartRepository,
                           FailureHistoryRepository failureHistoryRepository) {
        this.equipmentRepository = equipmentRepository;
        this.serviceTeamRepository = serviceTeamRepository;
        this.sparePartRepository = sparePartRepository;
        this.failureHistoryRepository = failureHistoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (equipmentRepository.count() == 0) {
            
            // 1. Инициализируем и сохраняем оборудование
            Equipment eq = new Equipment();
            eq.setName("Токарный станок ЧПУ");
            eq.setStatus("Работает");
            eq.setFailureHistory(new ArrayList<>());
            eq.setSpareParts(new ArrayList<>());
            eq = equipmentRepository.save(eq);

            // 2. Инициализируем доступную ремонтную бригаду
            ServiceTeam team = new ServiceTeam();
            team.setName("Бригада №1 (Слесари)");
            team.setMaxActiveOrders(5);
            team.setCurrentLoad(0);
            serviceTeamRepository.save(team);

            // 3. Создаем деталь и явно привязываем её к созданному станку
            SparePart part = new SparePart();
            part.setEquipment(eq);
            part.setName("Приводной ремень");
            part.setStockQuantity(12);
            sparePartRepository.save(part);

            // Обновляем список деталей у самого оборудования для сохранения связи в кэше Hibernate
            eq.getSpareParts().add(part);

            // 4. Добавляем историю отказов для вычисления рисков и OEE
            FailureHistory failure = new FailureHistory();
            failure.setEquipment(eq);
            failure.setFailureDate(LocalDate.now().minusDays(3));
            failure.setDescription("Критический перегрев шпинделя");
            failure.setDowntimeHours(8);
            failureHistoryRepository.save(failure);
            
            eq.getFailureHistory().add(failure);
            equipmentRepository.save(eq);
            
            System.out.println(">>> Данные успешно загружены. ID оборудования для создания ордера: " + eq.getId());
        }
    }
}