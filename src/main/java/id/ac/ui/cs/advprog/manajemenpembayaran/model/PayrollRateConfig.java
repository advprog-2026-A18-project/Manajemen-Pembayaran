package id.ac.ui.cs.advprog.manajemenpembayaran.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payroll_rate_configs")
public class PayrollRateConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal buruhRatePerKg;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal supirRatePerKg;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal mandorRatePerKg;

    @Column(nullable = false)
    private LocalDateTime effectiveFrom;

    @PrePersist
    void onCreate() {
        if (effectiveFrom == null) {
            effectiveFrom = LocalDateTime.now();
        }
    }
}
