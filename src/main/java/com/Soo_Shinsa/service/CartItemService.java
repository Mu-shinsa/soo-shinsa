package com.Soo_Shinsa.service;


import com.Soo_Shinsa.dto.CartItemResponseDto;
import com.Soo_Shinsa.entity.CartItem;


public interface CartItemService {
    CartItemResponseDto create(Long optionId,Integer quantity,Long userId);
    CartItemResponseDto findById(Long cartId,Long userId);
    CartItem findByIdOrElseThrow(Long id);
    CartItemResponseDto update(Long cartId,Long userId,Integer quantity);

    void delete(Long cartId,Long userId);
}
