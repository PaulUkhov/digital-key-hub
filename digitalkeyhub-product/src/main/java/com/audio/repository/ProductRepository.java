package com.audio.repository;

import com.audio.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    @Query("SELECT p FROM ProductEntity p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:isActive IS NULL OR p.isActive = :isActive)")
    Page<ProductEntity> findByFilters(
            @Param("name") String name,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("isActive") Boolean isActive,
            Pageable pageable);
}
