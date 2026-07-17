package org.example.finreport.module.business.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trans_log")
@Getter @Setter
public class TransLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transNo;
    private String accountNo;
    private String customerNo;
    private String transType;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String channel;
    private String status;
    private LocalDateTime transTime;
    private String description;
    private LocalDateTime createdAt;
}
