package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollRateRequest;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRateConfigRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PayrollRateConfigService {

    private final PayrollRateConfigRepository payrollRateConfigRepository;

    public PayrollRateConfigService(PayrollRateConfigRepository payrollRateConfigRepository) {
        this.payrollRateConfigRepository = payrollRateConfigRepository;
    }

    public PayrollRateConfig upsert(PayrollRateRequest request) {
        PayrollRateConfig config = payrollRateConfigRepository.findTopByOrderByEffectiveFromDesc()
                .orElseGet(PayrollRateConfig::new);

        config.setBuruhRatePerKg(request.getBuruhRatePerKg());
        config.setSupirRatePerKg(request.getSupirRatePerKg());
        config.setMandorRatePerKg(request.getMandorRatePerKg());
        config.setEffectiveFrom(LocalDateTime.now());

        return payrollRateConfigRepository.save(config);
    }

    public PayrollRateConfig getCurrentRate() {
        return payrollRateConfigRepository.findTopByOrderByEffectiveFromDesc().orElse(null);
    }
}
