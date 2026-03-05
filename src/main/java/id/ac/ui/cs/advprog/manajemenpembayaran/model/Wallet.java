package id.ac.ui.cs.advprog.manajemenpembayaran.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ownerId;

    @Column(nullable = false)
    private String ownerRole;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @PrePersist
    void onCreate() {
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
    }
}
