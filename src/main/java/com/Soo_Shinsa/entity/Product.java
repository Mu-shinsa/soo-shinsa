package com.Soo_Shinsa.entity;

import com.Soo_Shinsa.model.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Length(min = 1, max = 255)
    private String name;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Length(min = 1, max = 15)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_Id", nullable = false)
    private Brand brandId;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ProductOption> productOptions = new ArrayList<>();

    private Product(Long id, String name, BigDecimal price, String status, Brand brandId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = status;
        this.brandId = brandId;
    }
}
