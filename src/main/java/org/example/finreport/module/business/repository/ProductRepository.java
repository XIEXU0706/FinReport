package org.example.finreport.module.business.repository;

import org.example.finreport.module.business.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
