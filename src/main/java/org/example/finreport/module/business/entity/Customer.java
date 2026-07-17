package org.example.finreport.module.business.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Getter @Setter
public class Customer {
    @Id
    private Long id;
    private String customerNo;
    private String name;
    private String idType;
    private String idNumber;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String customerLevel;
    private java.time.LocalDate birthDate;
    private String occupation;
    private Long branchId;
    private String status;
    private LocalDateTime openDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
