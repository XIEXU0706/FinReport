package org.example.finreport.module.business.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter @Setter
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productCode;
    private String productName;
    private String productType;
    private String riskLevel;
    private BigDecimal expectedReturn;
    private BigDecimal minAmount;
    private Integer termDays;
    private String status;
    private LocalDateTime createdAt;
}
