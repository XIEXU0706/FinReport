package org.example.finreport.module.business.repository;

import org.example.finreport.module.business.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
