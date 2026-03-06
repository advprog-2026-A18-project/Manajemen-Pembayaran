package id.ac.ui.cs.advprog.manajemenpembayaran.config;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRateConfigRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PaymentDataInitializer implements CommandLineRunner {

    private final PayrollRateConfigRepository payrollRateConfigRepository;
    private final WalletRepository walletRepository;

    @Value("${payment.seed.buruh-rate:1000}")
    private BigDecimal defaultBuruhRate;

    @Value("${payment.seed.supir-rate:1000}")
    private BigDecimal defaultSupirRate;

    @Value("${payment.seed.mandor-rate:1000}")
    private BigDecimal defaultMandorRate;

    public PaymentDataInitializer(PayrollRateConfigRepository payrollRateConfigRepository,
                                  WalletRepository walletRepository) {
        this.payrollRateConfigRepository = payrollRateConfigRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public void run(String... args) {
        seedDefaultPayrollRateIfMissing();
        seedDefaultWalletsIfMissing();
    }

    void seedDefaultPayrollRateIfMissing() {
        if (payrollRateConfigRepository.findTopByOrderByEffectiveFromDesc().isPresent()) {
            return;
        }

        PayrollRateConfig defaultRate = PayrollRateConfig.builder()
                .buruhRatePerKg(defaultBuruhRate)
                .supirRatePerKg(defaultSupirRate)
                .mandorRatePerKg(defaultMandorRate)
                .build();

        payrollRateConfigRepository.save(defaultRate);
    }

    void seedDefaultWalletsIfMissing() {
        List<Wallet> defaults = List.of(
                Wallet.builder().ownerId("admin-default").ownerRole("ADMIN").balance(BigDecimal.ZERO).build(),
                Wallet.builder().ownerId("mandor-default").ownerRole("MANDOR").balance(BigDecimal.ZERO).build(),
                Wallet.builder().ownerId("buruh-default").ownerRole("BURUH").balance(BigDecimal.ZERO).build(),
                Wallet.builder().ownerId("supir-default").ownerRole("SUPIR").balance(BigDecimal.ZERO).build()
        );

        for (Wallet wallet : defaults) {
            if (walletRepository.findByOwnerId(wallet.getOwnerId()).isEmpty()) {
                walletRepository.save(wallet);
            }
        }
    }
}
