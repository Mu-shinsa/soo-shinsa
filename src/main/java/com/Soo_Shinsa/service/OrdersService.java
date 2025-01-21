package com.Soo_Shinsa.service;

import com.Soo_Shinsa.constant.OrdersStatus;
import com.Soo_Shinsa.dto.OrdersResponseDto;
import com.Soo_Shinsa.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OrdersService {
    OrdersResponseDto getOrderById(Long orderId, User user);
    Page<OrdersResponseDto> getAllByUserId(User user, Pageable pageable);
    OrdersResponseDto createSingleProductOrder(User user, Long productId, Integer quantity);
    OrdersResponseDto createOrderFromCart(User user);

    OrdersResponseDto createOrder (User user);

    OrdersResponseDto updateOrder (User user, Long orderId, OrdersStatus status);
}
