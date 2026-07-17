package org.example.finreport.module.business.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_record")
@Getter @Setter
public class LoanRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loanNo;
    private String customerNo;
    private BigDecimal amount;
    private Integer termMonths;
    private BigDecimal interestRate;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private String repayMethod;
    private BigDecimal maturityInterest;
    private BigDecimal maturityTotal;
    private LocalDate repayDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
