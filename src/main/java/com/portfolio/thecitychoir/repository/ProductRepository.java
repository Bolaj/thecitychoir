package com.portfolio.thecitychoir.repository;

import com.portfolio.thecitychoir.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
