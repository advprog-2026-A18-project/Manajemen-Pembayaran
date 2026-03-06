package id.ac.ui.cs.advprog.manajemenpembayaran.config;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRateConfigRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentDataInitializerTest {

    @Mock
    private PayrollRateConfigRepository payrollRateConfigRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private PaymentDataInitializer initializer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(initializer, "defaultBuruhRate", BigDecimal.valueOf(2000));
        ReflectionTestUtils.setField(initializer, "defaultSupirRate", BigDecimal.valueOf(1500));
        ReflectionTestUtils.setField(initializer, "defaultMandorRate", BigDecimal.valueOf(2500));
    }

    @Test
    void runShouldSeedRateAndWalletsWhenMissing() {
        when(payrollRateConfigRepository.findTopByOrderByEffectiveFromDesc()).thenReturn(Optional.empty());
        when(walletRepository.findByOwnerId(anyString())).thenReturn(Optional.empty());

        initializer.run();

        ArgumentCaptor<PayrollRateConfig> rateCaptor = ArgumentCaptor.forClass(PayrollRateConfig.class);
        verify(payrollRateConfigRepository, times(1)).save(rateCaptor.capture());

        PayrollRateConfig savedRate = rateCaptor.getValue();
        assertEquals(BigDecimal.valueOf(2000), savedRate.getBuruhRatePerKg());
        assertEquals(BigDecimal.valueOf(1500), savedRate.getSupirRatePerKg());
        assertEquals(BigDecimal.valueOf(2500), savedRate.getMandorRatePerKg());

        verify(walletRepository, times(4)).save(any(Wallet.class));
    }

    @Test
    void runShouldNotSeedWhenDataAlreadyExists() {
        when(payrollRateConfigRepository.findTopByOrderByEffectiveFromDesc())
                .thenReturn(Optional.of(PayrollRateConfig.builder().id(1L).build()));
        when(walletRepository.findByOwnerId(anyString()))
                .thenReturn(Optional.of(Wallet.builder().id(1L).build()));

        initializer.run();

        verify(payrollRateConfigRepository, never()).save(any(PayrollRateConfig.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}
