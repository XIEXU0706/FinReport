package org.example.finreport.module.business.repository;

import org.example.finreport.module.business.entity.TransLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransLogRepository extends JpaRepository<TransLog, Long> {
}
