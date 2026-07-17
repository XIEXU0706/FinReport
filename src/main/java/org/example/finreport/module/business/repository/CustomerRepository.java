package org.example.finreport.module.business.repository;

import org.example.finreport.module.business.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
