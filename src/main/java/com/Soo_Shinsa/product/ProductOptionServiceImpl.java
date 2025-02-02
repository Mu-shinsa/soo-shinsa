package com.Soo_Shinsa.product;

import com.Soo_Shinsa.product.dto.FindProductOptionRequestDto;
import com.Soo_Shinsa.product.dto.ProductOptionRequestDto;
import com.Soo_Shinsa.product.dto.ProductOptionResponseDto;
import com.Soo_Shinsa.product.dto.ProductOptionUpdateDto;
import com.Soo_Shinsa.product.model.Product;
import com.Soo_Shinsa.product.model.ProductOption;
import com.Soo_Shinsa.user.model.User;
import com.Soo_Shinsa.utils.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductOptionServiceImpl implements ProductOptionService {

   private final ProductOptionRepository productOptionRepository;
   private final ProductRepository productRepository;

    @Transactional
    @Override
    public ProductOptionResponseDto createOption(User user, ProductOptionRequestDto dto, Long productId) {
        EntityValidator.validateAdminOrVendorAccess(user);

        Product findProduct = productRepository.findByIdOrElseThrow(productId);


        ProductOption option = dto.toEntity(findProduct);

        ProductOption savedOption = productOptionRepository.save(option);

        return ProductOptionResponseDto.toDto(savedOption);
    }

    @Transactional
    @Override
    public ProductOptionResponseDto updateOption(User user, ProductOptionUpdateDto dto, Long productOptionId) {

        EntityValidator.validateAdminOrVendorAccess(user);

        ProductOption findOption = productOptionRepository.findByIdOrElseThrow(productOptionId);

        Product associatedProduct = findOption.getProduct();

        if (associatedProduct == null) {
            throw new IllegalArgumentException("옵션에 연관된 상품이 없습니다.");
        }

        findOption.update(dto.getSize(), dto.getColor(), dto.getStatus());


        return ProductOptionResponseDto.toDto(findOption);
    }

    @Override
    public ProductOptionResponseDto findOption(Long productOptionId) {

        ProductOption findOption = productOptionRepository.findByIdOrElseThrow(productOptionId);

        return ProductOptionResponseDto.toDto(findOption);
    }

    @Override
    public Page<ProductOptionResponseDto> findProductsByOptionalSizeAndColor(FindProductOptionRequestDto requestDto, int page, int size) {

        if (requestDto.getColor() == null && requestDto.getSize() == null) {
            throw new IllegalArgumentException("색상과 사이즈 중 하나는 필수입니다.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductOption> options = productOptionRepository.findProductsByOptionalSizeAndColor(requestDto.getSize(), requestDto.getColor(), pageable);

        return options.map(ProductOptionResponseDto::toDto);
    }

}

