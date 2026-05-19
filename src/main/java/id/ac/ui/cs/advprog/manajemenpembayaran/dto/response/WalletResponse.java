package id.ac.ui.cs.advprog.manajemenpembayaran.dto.response;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class WalletResponse {
    private String ownerId;
    private String ownerRole;
    private BigDecimal balance;

    public static WalletResponse from(Wallet wallet) {
        return WalletResponse.builder()
                .ownerId(wallet.getOwnerId())
                .ownerRole(wallet.getOwnerRole())
                .balance(wallet.getBalance())
                .build();
    }
}
