package org.example.finreport.module.business.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prod_sale")
@Getter @Setter
public class ProdSale {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String saleNo;
    @Column(length = 32)
    private String productNo;
    @Column(length = 32)
    private String customerNo;
    private BigDecimal amount;
    private LocalDate saleDate;
    private String status;
    private Long branchId;
    private LocalDateTime createdAt;
}
