package id.ac.ui.cs.advprog.manajemenpembayaran.integration;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.HarvestApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentAdminApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentMandorApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRateConfigRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollCalculatorService;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollEventService;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollRateConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import({PayrollEventService.class, PayrollCalculatorService.class, PayrollRateConfigService.class})
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class PayrollEventIntegrationTest {

    @Autowired
    private PayrollEventService payrollEventService;

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private PayrollRateConfigRepository payrollRateConfigRepository;

    @BeforeEach
    void setUp() {
        payrollRepository.deleteAll();
        payrollRateConfigRepository.deleteAll();

        payrollRateConfigRepository.save(PayrollRateConfig.builder()
                .buruhRatePerKg(new BigDecimal("2000"))
                .supirRatePerKg(new BigDecimal("1500"))
                .mandorRatePerKg(new BigDecimal("2500"))
                .build());
    }

    @Test
    void harvestEventShouldCreatePayrollOnlyOnceForSameEventId() {
        HarvestApprovedEvent event = new HarvestApprovedEvent();
        event.setEventId("evt-h-100");
        event.setBuruhId("buruh-100");
        event.setKilogram(new BigDecimal("100"));
        event.setApprovedAt(LocalDateTime.now());

        Payroll first = payrollEventService.processHarvestApproved(event);
        Payroll second = payrollEventService.processHarvestApproved(event);

        assertEquals(first.getId(), second.getId());
        assertEquals(1, payrollRepository.count());
        assertEquals(new BigDecimal("180000.00"), first.getAmount());
    }

    @Test
    void shipmentMandorEventShouldCreatePayrollOnlyOnceForSameEventId() {
        ShipmentMandorApprovedEvent event = new ShipmentMandorApprovedEvent();
        event.setEventId("evt-s-100");
        event.setSupirId("supir-100");
        event.setKilogram(new BigDecimal("50"));
        event.setApprovedAt(LocalDateTime.now());

        Payroll first = payrollEventService.processShipmentMandorApproved(event);
        Payroll second = payrollEventService.processShipmentMandorApproved(event);

        assertEquals(first.getId(), second.getId());
        assertEquals(1, payrollRepository.count());
        assertEquals(new BigDecimal("67500.00"), first.getAmount());
    }

    @Test
    void shipmentAdminEventShouldCreatePayrollOnlyOnceForSameEventId() {
        ShipmentAdminApprovedEvent event = new ShipmentAdminApprovedEvent();
        event.setEventId("evt-a-100");
        event.setMandorId("mandor-100");
        event.setKilogramDiakui(new BigDecimal("80"));
        event.setApprovedAt(LocalDateTime.now());

        Payroll first = payrollEventService.processShipmentAdminApproved(event);
        Payroll second = payrollEventService.processShipmentAdminApproved(event);

        assertEquals(first.getId(), second.getId());
        assertEquals(1, payrollRepository.count());
        assertEquals(new BigDecimal("180000.00"), first.getAmount());
    }
}
