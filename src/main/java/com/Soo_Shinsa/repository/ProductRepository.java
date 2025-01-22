package com.Soo_Shinsa.repository;

import com.Soo_Shinsa.model.Brand;
import com.Soo_Shinsa.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository  extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);

    List<Product> findAllByBrandId(Brand brandId);
}
