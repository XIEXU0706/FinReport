package org.example.finreport.module.business.repository;

import org.example.finreport.module.business.entity.LoanRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRecordRepository extends JpaRepository<LoanRecord, Long> {
}
