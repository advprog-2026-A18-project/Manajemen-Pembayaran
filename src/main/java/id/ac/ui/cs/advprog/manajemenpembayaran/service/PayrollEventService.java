package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.constant.PaymentConstants;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollCalculationResult;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollEventProcessResult;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.HarvestApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentAdminApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentMandorApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollSourceType;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PayrollEventService {

    private final PayrollRepository payrollRepository;
    private final WalletRepository walletRepository;
    private final PayrollRateConfigService payrollRateConfigService;
    private final PayrollCalculatorService payrollCalculatorService;

    public PayrollEventService(PayrollRepository payrollRepository,
                               WalletRepository walletRepository,
                               PayrollRateConfigService payrollRateConfigService,
                               PayrollCalculatorService payrollCalculatorService) {
        this.payrollRepository = payrollRepository;
        this.walletRepository = walletRepository;
        this.payrollRateConfigService = payrollRateConfigService;
        this.payrollCalculatorService = payrollCalculatorService;
    }

    public Payroll processHarvestApproved(HarvestApprovedEvent event) {
        return processHarvestApprovedWithResult(event).getPayroll();
    }

    public PayrollEventProcessResult processHarvestApprovedWithResult(HarvestApprovedEvent event) {
        String key = buildIdempotencyKey(PayrollSourceType.HARVEST_APPROVAL, event.getEventId());
        return payrollRepository.findByIdempotencyKey(key)
                .map(existing -> PayrollEventProcessResult.builder()
                        .payroll(existing)
                        .created(false)
                        .build())
                .orElseGet(() -> PayrollEventProcessResult.builder()
                        .payroll(createPayrollForBuruh(event, key))
                        .created(true)
                        .build());
    }

    public Payroll processShipmentMandorApproved(ShipmentMandorApprovedEvent event) {
        return processShipmentMandorApprovedWithResult(event).getPayroll();
    }

    public PayrollEventProcessResult processShipmentMandorApprovedWithResult(ShipmentMandorApprovedEvent event) {
        String key = buildIdempotencyKey(PayrollSourceType.SHIPMENT_MANDOR_APPROVAL, event.getEventId());
        return payrollRepository.findByIdempotencyKey(key)
                .map(existing -> PayrollEventProcessResult.builder()
                        .payroll(existing)
                        .created(false)
                        .build())
                .orElseGet(() -> PayrollEventProcessResult.builder()
                        .payroll(createPayrollForSupir(event, key))
                        .created(true)
                        .build());
    }

    public Payroll processShipmentAdminApproved(ShipmentAdminApprovedEvent event) {
        return processShipmentAdminApprovedWithResult(event).getPayroll();
    }

    public PayrollEventProcessResult processShipmentAdminApprovedWithResult(ShipmentAdminApprovedEvent event) {
        String key = buildIdempotencyKey(PayrollSourceType.SHIPMENT_ADMIN_APPROVAL, event.getEventId());
        return payrollRepository.findByIdempotencyKey(key)
                .map(existing -> PayrollEventProcessResult.builder()
                        .payroll(existing)
                        .created(false)
                        .build())
                .orElseGet(() -> PayrollEventProcessResult.builder()
                        .payroll(createPayrollForMandor(event, key))
                        .created(true)
                        .build());
    }

    private Payroll createPayrollForBuruh(HarvestApprovedEvent event, String key) {
        validateEvent(event.getEventId(), event.getBuruhId());
        PayrollRateConfig rateConfig = getRateConfig();
        PayrollCalculationResult result = payrollCalculatorService
                .calculateBuruh(event.getKilogram(), rateConfig.getBuruhRatePerKg());
        ensureWalletExists(event.getBuruhId(), PaymentConstants.Role.BURUH);

        return payrollRepository.save(Payroll.builder()
                .ownerId(event.getBuruhId())
                .ownerRole(PaymentConstants.Role.BURUH)
                .kilogram(result.getKgUsed())
                .rateUsed(result.getRateUsed())
                .amount(result.getAmount())
                .sourceType(PayrollSourceType.HARVEST_APPROVAL)
                .sourceId(event.getEventId())
                .idempotencyKey(key)
                .description(result.getFormulaDescription())
                .build());
    }

    private Payroll createPayrollForSupir(ShipmentMandorApprovedEvent event, String key) {
        validateEvent(event.getEventId(), event.getSupirId());
        PayrollRateConfig rateConfig = getRateConfig();
        PayrollCalculationResult result = payrollCalculatorService
                .calculateSupir(event.getKilogram(), rateConfig.getSupirRatePerKg());
        ensureWalletExists(event.getSupirId(), PaymentConstants.Role.SUPIR);

        return payrollRepository.save(Payroll.builder()
                .ownerId(event.getSupirId())
                .ownerRole(PaymentConstants.Role.SUPIR)
                .kilogram(result.getKgUsed())
                .rateUsed(result.getRateUsed())
                .amount(result.getAmount())
                .sourceType(PayrollSourceType.SHIPMENT_MANDOR_APPROVAL)
                .sourceId(event.getEventId())
                .idempotencyKey(key)
                .description(result.getFormulaDescription())
                .build());
    }

    private Payroll createPayrollForMandor(ShipmentAdminApprovedEvent event, String key) {
        validateEvent(event.getEventId(), event.getMandorId());
        PayrollRateConfig rateConfig = getRateConfig();
        PayrollCalculationResult result = payrollCalculatorService
                .calculateMandor(event.getKilogramDiakui(), rateConfig.getMandorRatePerKg());
        ensureWalletExists(event.getMandorId(), PaymentConstants.Role.MANDOR);

        return payrollRepository.save(Payroll.builder()
                .ownerId(event.getMandorId())
                .ownerRole(PaymentConstants.Role.MANDOR)
                .kilogram(result.getKgUsed())
                .rateUsed(result.getRateUsed())
                .amount(result.getAmount())
                .sourceType(PayrollSourceType.SHIPMENT_ADMIN_APPROVAL)
                .sourceId(event.getEventId())
                .idempotencyKey(key)
                .description(result.getFormulaDescription())
                .build());
    }

    private PayrollRateConfig getRateConfig() {
        PayrollRateConfig config = payrollRateConfigService.getCurrentRate();
        if (config == null) {
            throw new IllegalStateException("Payroll rate config is not set");
        }
        return config;
    }

    private String buildIdempotencyKey(PayrollSourceType sourceType, String eventId) {
        return sourceType.name() + ":" + eventId;
    }

    private void validateEvent(String eventId, String ownerId) {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId is required");
        }
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("ownerId is required");
        }
    }

    private void ensureWalletExists(String ownerId, String ownerRole) {
        if (walletRepository.findByOwnerId(ownerId).isPresent()) {
            return;
        }

        walletRepository.save(Wallet.builder()
                .ownerId(ownerId)
                .ownerRole(ownerRole)
                .balance(BigDecimal.ZERO)
                .build());
    }
}
