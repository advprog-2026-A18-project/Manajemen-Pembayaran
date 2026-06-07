package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollCalculationResult;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.HarvestApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentAdminApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentMandorApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollSourceType;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollEventServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PayrollRateConfigService payrollRateConfigService;

    @Mock
    private PayrollCalculatorService payrollCalculatorService;

    @InjectMocks
    private PayrollEventService payrollEventService;

    private HarvestApprovedEvent harvestEvent;
    private ShipmentMandorApprovedEvent shipmentMandorEvent;
    private ShipmentAdminApprovedEvent shipmentAdminEvent;

    @BeforeEach
    void setUp() {
        harvestEvent = new HarvestApprovedEvent();
        harvestEvent.setEventId("evt-h-1");
        harvestEvent.setBuruhId("buruh-1");
        harvestEvent.setKilogram(new BigDecimal("100"));

        shipmentMandorEvent = new ShipmentMandorApprovedEvent();
        shipmentMandorEvent.setEventId("evt-s-1");
        shipmentMandorEvent.setSupirId("supir-1");
        shipmentMandorEvent.setKilogram(new BigDecimal("50"));

        shipmentAdminEvent = new ShipmentAdminApprovedEvent();
        shipmentAdminEvent.setEventId("evt-a-1");
        shipmentAdminEvent.setMandorId("mandor-1");
        shipmentAdminEvent.setKilogramDiakui(new BigDecimal("80"));
    }

    @Test
    void processHarvestApprovedShouldCreatePayrollWhenEventIsNew() {
        PayrollRateConfig rateConfig = PayrollRateConfig.builder().buruhRatePerKg(new BigDecimal("2000")).build();
        PayrollCalculationResult calcResult = PayrollCalculationResult.builder()
                .kgUsed(new BigDecimal("100"))
                .rateUsed(new BigDecimal("2000"))
                .amount(new BigDecimal("180000.00"))
                .formulaDescription("Upah Buruh = rate * kilogram * 90%")
                .build();

        when(payrollRepository.findByIdempotencyKey("HARVEST_APPROVAL:evt-h-1")).thenReturn(Optional.empty());
        when(payrollRateConfigService.getCurrentRate()).thenReturn(rateConfig);
        when(payrollCalculatorService.calculateBuruh(new BigDecimal("100"), new BigDecimal("2000"))).thenReturn(calcResult);
        when(walletRepository.findByOwnerId("buruh-1")).thenReturn(Optional.empty());
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payroll result = payrollEventService.processHarvestApproved(harvestEvent);

        assertEquals("buruh-1", result.getOwnerId());
        assertEquals("BURUH", result.getOwnerRole());
        assertEquals(PayrollSourceType.HARVEST_APPROVAL, result.getSourceType());
        assertEquals("HARVEST_APPROVAL:evt-h-1", result.getIdempotencyKey());

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(walletCaptor.capture());
        assertEquals("buruh-1", walletCaptor.getValue().getOwnerId());
        assertEquals("BURUH", walletCaptor.getValue().getOwnerRole());
        assertEquals(BigDecimal.ZERO, walletCaptor.getValue().getBalance());
    }

    @Test
    void processShipmentMandorApprovedShouldCreatePayrollWhenEventIsNew() {
        PayrollRateConfig rateConfig = PayrollRateConfig.builder().supirRatePerKg(new BigDecimal("1500")).build();
        PayrollCalculationResult calcResult = PayrollCalculationResult.builder()
                .kgUsed(new BigDecimal("50"))
                .rateUsed(new BigDecimal("1500"))
                .amount(new BigDecimal("67500.00"))
                .formulaDescription("Upah Supir = rate * kilogram * 90%")
                .build();

        when(payrollRepository.findByIdempotencyKey("SHIPMENT_MANDOR_APPROVAL:evt-s-1")).thenReturn(Optional.empty());
        when(payrollRateConfigService.getCurrentRate()).thenReturn(rateConfig);
        when(payrollCalculatorService.calculateSupir(new BigDecimal("50"), new BigDecimal("1500"))).thenReturn(calcResult);
        when(walletRepository.findByOwnerId("supir-1")).thenReturn(Optional.empty());
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payroll result = payrollEventService.processShipmentMandorApproved(shipmentMandorEvent);

        assertEquals("supir-1", result.getOwnerId());
        assertEquals("SUPIR", result.getOwnerRole());
        assertEquals(PayrollSourceType.SHIPMENT_MANDOR_APPROVAL, result.getSourceType());
        assertEquals("SHIPMENT_MANDOR_APPROVAL:evt-s-1", result.getIdempotencyKey());

        verify(walletRepository).save(argThat(wallet ->
                "supir-1".equals(wallet.getOwnerId()) && "SUPIR".equals(wallet.getOwnerRole())));
    }

    @Test
    void processShipmentAdminApprovedShouldCreatePayrollWhenEventIsNew() {
        PayrollRateConfig rateConfig = PayrollRateConfig.builder().mandorRatePerKg(new BigDecimal("2500")).build();
        PayrollCalculationResult calcResult = PayrollCalculationResult.builder()
                .kgUsed(new BigDecimal("80"))
                .rateUsed(new BigDecimal("2500"))
                .amount(new BigDecimal("180000.00"))
                .formulaDescription("Upah Mandor = rate * kilogram * 90%")
                .build();

        when(payrollRepository.findByIdempotencyKey("SHIPMENT_ADMIN_APPROVAL:evt-a-1")).thenReturn(Optional.empty());
        when(payrollRateConfigService.getCurrentRate()).thenReturn(rateConfig);
        when(payrollCalculatorService.calculateMandor(new BigDecimal("80"), new BigDecimal("2500"))).thenReturn(calcResult);
        when(walletRepository.findByOwnerId("mandor-1")).thenReturn(Optional.empty());
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payroll result = payrollEventService.processShipmentAdminApproved(shipmentAdminEvent);

        assertEquals("mandor-1", result.getOwnerId());
        assertEquals("MANDOR", result.getOwnerRole());
        assertEquals(PayrollSourceType.SHIPMENT_ADMIN_APPROVAL, result.getSourceType());
        assertEquals("SHIPMENT_ADMIN_APPROVAL:evt-a-1", result.getIdempotencyKey());

        ArgumentCaptor<Payroll> captor = ArgumentCaptor.forClass(Payroll.class);
        verify(payrollRepository).save(captor.capture());
        assertEquals(new BigDecimal("2500"), captor.getValue().getRateUsed());
        verify(walletRepository).save(argThat(wallet ->
                "mandor-1".equals(wallet.getOwnerId()) && "MANDOR".equals(wallet.getOwnerRole())));
    }

    @Test
    void processHarvestApprovedShouldNotCreateWalletWhenWalletAlreadyExists() {
        PayrollRateConfig rateConfig = PayrollRateConfig.builder().buruhRatePerKg(new BigDecimal("2000")).build();
        PayrollCalculationResult calcResult = PayrollCalculationResult.builder()
                .kgUsed(new BigDecimal("100"))
                .rateUsed(new BigDecimal("2000"))
                .amount(new BigDecimal("180000.00"))
                .formulaDescription("Upah Buruh = rate * kilogram * 90%")
                .build();
        Wallet existingWallet = Wallet.builder().ownerId("buruh-1").ownerRole("BURUH").balance(BigDecimal.ZERO).build();

        when(payrollRepository.findByIdempotencyKey("HARVEST_APPROVAL:evt-h-1")).thenReturn(Optional.empty());
        when(payrollRateConfigService.getCurrentRate()).thenReturn(rateConfig);
        when(payrollCalculatorService.calculateBuruh(new BigDecimal("100"), new BigDecimal("2000"))).thenReturn(calcResult);
        when(walletRepository.findByOwnerId("buruh-1")).thenReturn(Optional.of(existingWallet));
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payroll result = payrollEventService.processHarvestApproved(harvestEvent);

        assertEquals("buruh-1", result.getOwnerId());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void processHarvestApprovedShouldReturnExistingPayrollWhenDuplicateEvent() {
        Payroll existing = Payroll.builder().id(1L).idempotencyKey("HARVEST_APPROVAL:evt-h-1").build();
        when(payrollRepository.findByIdempotencyKey("HARVEST_APPROVAL:evt-h-1")).thenReturn(Optional.of(existing));

        Payroll result = payrollEventService.processHarvestApproved(harvestEvent);

        assertEquals(1L, result.getId());
        verify(payrollRepository, never()).save(any(Payroll.class));
    }

    @Test
    void processShipmentMandorApprovedShouldReturnExistingPayrollWhenDuplicateEvent() {
        Payroll existing = Payroll.builder().id(2L).idempotencyKey("SHIPMENT_MANDOR_APPROVAL:evt-s-1").build();
        when(payrollRepository.findByIdempotencyKey("SHIPMENT_MANDOR_APPROVAL:evt-s-1")).thenReturn(Optional.of(existing));

        Payroll result = payrollEventService.processShipmentMandorApproved(shipmentMandorEvent);

        assertEquals(2L, result.getId());
        verify(payrollRepository, never()).save(any(Payroll.class));
    }

    @Test
    void processShipmentAdminApprovedShouldReturnExistingPayrollWhenDuplicateEvent() {
        Payroll existing = Payroll.builder().id(3L).idempotencyKey("SHIPMENT_ADMIN_APPROVAL:evt-a-1").build();
        when(payrollRepository.findByIdempotencyKey("SHIPMENT_ADMIN_APPROVAL:evt-a-1")).thenReturn(Optional.of(existing));

        Payroll result = payrollEventService.processShipmentAdminApproved(shipmentAdminEvent);

        assertEquals(3L, result.getId());
        verify(payrollRepository, never()).save(any(Payroll.class));
    }

    @Test
    void processHarvestApprovedShouldThrowWhenRateConfigMissing() {
        when(payrollRepository.findByIdempotencyKey("HARVEST_APPROVAL:evt-h-1")).thenReturn(Optional.empty());
        when(payrollRateConfigService.getCurrentRate()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> payrollEventService.processHarvestApproved(harvestEvent));
    }

    @Test
    void processShipmentMandorApprovedShouldThrowWhenRateConfigMissing() {
        when(payrollRepository.findByIdempotencyKey("SHIPMENT_MANDOR_APPROVAL:evt-s-1")).thenReturn(Optional.empty());
        when(payrollRateConfigService.getCurrentRate()).thenReturn(null);

        assertThrows(IllegalStateException.class,
                () -> payrollEventService.processShipmentMandorApproved(shipmentMandorEvent));
    }

    @Test
    void processShipmentAdminApprovedShouldThrowWhenRateConfigMissing() {
        when(payrollRepository.findByIdempotencyKey("SHIPMENT_ADMIN_APPROVAL:evt-a-1")).thenReturn(Optional.empty());
        when(payrollRateConfigService.getCurrentRate()).thenReturn(null);

        assertThrows(IllegalStateException.class,
                () -> payrollEventService.processShipmentAdminApproved(shipmentAdminEvent));
    }

    @Test
    void processShipmentMandorApprovedShouldThrowWhenEventInvalid() {
        shipmentMandorEvent.setSupirId(" ");

        assertThrows(IllegalArgumentException.class,
                () -> payrollEventService.processShipmentMandorApproved(shipmentMandorEvent));
    }

    @Test
    void processShipmentAdminApprovedShouldThrowWhenEventInvalid() {
        shipmentAdminEvent.setEventId(" ");

        assertThrows(IllegalArgumentException.class,
                () -> payrollEventService.processShipmentAdminApproved(shipmentAdminEvent));
    }
}
