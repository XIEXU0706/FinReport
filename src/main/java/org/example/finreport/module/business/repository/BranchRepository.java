package org.example.finreport.module.business.repository;

import org.example.finreport.module.business.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}
