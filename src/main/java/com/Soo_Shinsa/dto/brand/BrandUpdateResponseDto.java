package com.Soo_Shinsa.dto.brand;

import com.Soo_Shinsa.constant.BrandStatus;
import com.Soo_Shinsa.model.Brand;

public class BrandUpdateResponseDto {

    private Long id;
    private String registrationNum;
    private String name;
    private String context;
    private BrandStatus status;

    public BrandUpdateResponseDto(Long id, String registrationNum, String name, String context, BrandStatus status) {
        this.id = id;
        this.registrationNum = registrationNum;
        this.name = name;
        this.context = context;
        this.status = status;
    }

    public static BrandUpdateResponseDto toDto(Brand brand) {
        return new BrandUpdateResponseDto(
                brand.getId(),
                brand.getRegistrationNum(),
                brand.getName(),
                brand.getContext(),
                brand.getStatus());
    }
}
