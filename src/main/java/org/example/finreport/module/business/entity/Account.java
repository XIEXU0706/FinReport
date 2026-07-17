package org.example.finreport.module.business.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter @Setter
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNo;
    private String customerNo;
    private String accountType;
    private String currency;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;
}
