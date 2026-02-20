package id.ac.ui.cs.advprog.manajemenpembayaran.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dummy_data")
public class DummyData {

    @Id
    private String id;
    private String message;
    private String status;
}