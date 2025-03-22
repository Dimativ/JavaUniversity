package org.lytvinenko.com.kastaparsinglytvinenko.repository;

import org.lytvinenko.com.kastaparsinglytvinenko.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
