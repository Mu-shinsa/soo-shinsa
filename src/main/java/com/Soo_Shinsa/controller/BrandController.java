package com.Soo_Shinsa.controller;

import com.Soo_Shinsa.dto.brand.BrandUpdateResponseDto;
import com.Soo_Shinsa.dto.brand.BrandRequestDto;
import com.Soo_Shinsa.dto.brand.BrandResponseDto;
import com.Soo_Shinsa.model.User;
import com.Soo_Shinsa.service.BrandService;
import com.Soo_Shinsa.service.UserService;
import com.Soo_Shinsa.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brands")
public class BrandController {

    private final UserService userService;
    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<BrandResponseDto> createBrand(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BrandRequestDto brandRequestDto
    ) {
        BrandResponseDto brandResponseDto = brandService.create(UserUtils.getUser(userDetails),brandRequestDto);
        return ResponseEntity.ok(brandResponseDto);
    }

    @PatchMapping("/{brandId}")
    public ResponseEntity<BrandUpdateResponseDto> updateBrand(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BrandRequestDto brandRequestDto,
            @PathVariable Long brandId
    ) {
        BrandUpdateResponseDto brandRefuseResponseDto = brandService.update(
                UserUtils.getUser(userDetails),
                brandRequestDto,
                brandId);
        return new ResponseEntity<>(brandRefuseResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{brandId}/owners/{userId}")
    public ResponseEntity<BrandResponseDto> getBrand(
            @PathVariable Long brandId,
            @PathVariable Long userId
    ) {
        BrandResponseDto findBrand = brandService.findBrandById(brandId, userId);
        return new ResponseEntity<>(findBrand, HttpStatus.OK);
    }

    @GetMapping("/owners/{userId}")
    public ResponseEntity<List<BrandResponseDto>> getAllBrandByUserId(
            @PathVariable Long userId
    ) {
        List<BrandResponseDto> getAllBrand = brandService.getAllByUserId(userId);
        return new ResponseEntity<>(getAllBrand, HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<BrandResponseDto>> getAllBrands() {
        List<BrandResponseDto> getAllBrand = brandService.getAll();
        return new ResponseEntity<>(getAllBrand, HttpStatus.OK);
    }
}
