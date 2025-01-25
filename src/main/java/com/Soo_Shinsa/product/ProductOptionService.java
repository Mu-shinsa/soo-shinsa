package com.Soo_Shinsa.product;

import com.Soo_Shinsa.product.dto.ProductOptionRequestDto;
import com.Soo_Shinsa.product.dto.ProductOptionResponseDto;
import com.Soo_Shinsa.product.dto.ProductOptionUpdateDto;
import com.Soo_Shinsa.user.model.User;
import org.springframework.data.domain.Page;

public interface ProductOptionService {
    ProductOptionResponseDto createOption(User user, ProductOptionRequestDto productOptionRequestDto, Long productId);

    ProductOptionResponseDto updateOption(User user, ProductOptionUpdateDto dto, Long productId);

    ProductOptionResponseDto findOption(Long productOptionId);

    Page<ProductOptionResponseDto> findProductsByOptionalSizeAndColor(ProductOptionRequestDto requestDto, int page, int size);
}