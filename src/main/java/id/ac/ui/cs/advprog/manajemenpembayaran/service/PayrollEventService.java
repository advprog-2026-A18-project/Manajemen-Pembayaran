package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollCalculationResult;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.HarvestApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentAdminApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentMandorApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollSourceType;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import org.springframework.stereotype.Service;

@Service
public class PayrollEventService {

    private final PayrollRepository payrollRepository;
    private final PayrollRateConfigService payrollRateConfigService;
    private final PayrollCalculatorService payrollCalculatorService;

    public PayrollEventService(PayrollRepository payrollRepository,
                               PayrollRateConfigService payrollRateConfigService,
                               PayrollCalculatorService payrollCalculatorService) {
        this.payrollRepository = payrollRepository;
        this.payrollRateConfigService = payrollRateConfigService;
        this.payrollCalculatorService = payrollCalculatorService;
    }

    public Payroll processHarvestApproved(HarvestApprovedEvent event) {
        String key = buildIdempotencyKey(PayrollSourceType.HARVEST_APPROVAL, event.getEventId());
        return payrollRepository.findByIdempotencyKey(key)
                .orElseGet(() -> createPayrollForBuruh(event, key));
    }

    public Payroll processShipmentMandorApproved(ShipmentMandorApprovedEvent event) {
        String key = buildIdempotencyKey(PayrollSourceType.SHIPMENT_MANDOR_APPROVAL, event.getEventId());
        return payrollRepository.findByIdempotencyKey(key)
                .orElseGet(() -> createPayrollForSupir(event, key));
    }

    public Payroll processShipmentAdminApproved(ShipmentAdminApprovedEvent event) {
        String key = buildIdempotencyKey(PayrollSourceType.SHIPMENT_ADMIN_APPROVAL, event.getEventId());
        return payrollRepository.findByIdempotencyKey(key)
                .orElseGet(() -> createPayrollForMandor(event, key));
    }

    private Payroll createPayrollForBuruh(HarvestApprovedEvent event, String key) {
        validateEvent(event.getEventId(), event.getBuruhId());
        PayrollRateConfig rateConfig = getRateConfig();
        PayrollCalculationResult result = payrollCalculatorService
                .calculateBuruh(event.getKilogram(), rateConfig.getBuruhRatePerKg());

        return payrollRepository.save(Payroll.builder()
                .ownerId(event.getBuruhId())
                .ownerRole("BURUH")
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

        return payrollRepository.save(Payroll.builder()
                .ownerId(event.getSupirId())
                .ownerRole("SUPIR")
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

        return payrollRepository.save(Payroll.builder()
                .ownerId(event.getMandorId())
                .ownerRole("MANDOR")
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
}
