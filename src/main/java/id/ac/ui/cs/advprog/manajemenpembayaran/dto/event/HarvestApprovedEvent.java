package id.ac.ui.cs.advprog.manajemenpembayaran.dto.event;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class HarvestApprovedEvent {
    private String eventId;
    private String buruhId;
    private BigDecimal kilogram;
    private LocalDateTime approvedAt;
}
