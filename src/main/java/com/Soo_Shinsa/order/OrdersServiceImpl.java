package com.Soo_Shinsa.order;


import com.Soo_Shinsa.cartitem.CartItemRepository;
import com.Soo_Shinsa.cartitem.dto.CartItemResponseDto;
import com.Soo_Shinsa.cartitem.model.CartItem;
import com.Soo_Shinsa.constant.OrdersStatus;


import com.Soo_Shinsa.order.dto.OrdersResponseDto;

import com.Soo_Shinsa.order.model.OrderItem;
import com.Soo_Shinsa.order.model.Orders;
import com.Soo_Shinsa.product.ProductRepository;
import com.Soo_Shinsa.product.model.Product;
import com.Soo_Shinsa.user.UserRepository;
import com.Soo_Shinsa.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {
    private final ProductRepository productRepository;
    private final OrdersRepository ordersRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;


    //주문을 찾아오고 주문이 없다면 예외를 던지고 dto로 변환
    @Override
    public OrdersResponseDto getOrderById(Long orderId, User user) {
        User findUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다."));
        //오더를 찾아옴
        Orders findOrder = ordersRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 오더가 아닙니다."));;

        findUser.validateAndOrders(findOrder);
        //주문이 없을시 예외 던짐

        // OrdersResponseDto의 toDto 메서드 사용
        return OrdersResponseDto.toDto(findOrder);
    }

    @Override
    public Page<OrdersResponseDto> getAllByUserId(User user, Pageable pageable) {

        //오더를 찾아옴
        Page<Orders> allByUserUserId = ordersRepository.findAllByUserUserId(user.getUserId(),pageable);

        //주문이 없을시 예외 던짐

        if (allByUserUserId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orders를 찾을 수 없습니다");
        }
        // OrdersResponseDto의 toDto 메서드 사용
        return allByUserUserId.map(OrdersResponseDto::toDto);
    }


//
//    단일 상품 구매
//    상품을 찾아와서 주문번호를 생성 후 주문을 만들고 거기에 주문아이템에 물건을 담음
    @Transactional
    @Override
    public OrdersResponseDto createSingleProductOrder(User user,Long productId, Integer quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        //주문번호를 생성 후 주문을 만들고
        // Orders 생성
        Orders order = new Orders(product.getPrice().multiply(BigDecimal.valueOf(quantity)), OrdersStatus.BEFOREPAYMENT, user, new ArrayList<>());


        //주문아이템을생성
        OrderItem orderItem = new OrderItem(quantity, order, product);
        //오더에 오더아이템을 담음
        order.addOrderItem(orderItem);

        // Orders 저장함
        ordersRepository.save(order);

        // OrdersResponseDto로 변환 후 반환
        return OrdersResponseDto.toDto(order);
    }

    @Transactional
    @Override
    public OrdersResponseDto createOrderFromCart(User user,Pageable pageable) {
        // 사용자 확인

        List<CartItem> byUserUserId = cartItemRepository.findByUserUserId(user.getUserId());
        if (byUserUserId.isEmpty()) {
            throw new IllegalArgumentException("카트에 담긴 상품이 없습니다.");
        }

        // Orders 생성
        Orders order = new Orders(OrdersStatus.BEFOREPAYMENT, user, new ArrayList<>());


        // CartItem 데이터를 기반으로 OrderItem 생성 및 추가
        for (CartItem cartItem : byUserUserId) {
            Product product = cartItem.getProductOption().getProduct();
            Integer quantity = cartItem.getQuantity();

            // OrderItem 생성
            OrderItem orderItem = new OrderItem(quantity, order, product);
            order.addOrderItem(orderItem); // Order에 추가 및 총 금액 계산
        }

        // Orders 저장
        ordersRepository.save(order);

        // 카트 비우기
        cartItemRepository.deleteAll(byUserUserId);

        // OrdersResponseDto로 변환하여 반환
        return OrdersResponseDto.toDto(order);
    }

    @Transactional
    @Override
    public OrdersResponseDto createOrder(User user) {

        Orders order = new Orders(BigDecimal.ZERO, OrdersStatus.BEFOREPAYMENT,user, new ArrayList<>());
        // 주문 저장
        Orders savedOrder = ordersRepository.save(order);


        // ResponseDto로 변환
        return OrdersResponseDto.toDto(savedOrder);
    }
    @Transactional
    @Override
    public OrdersResponseDto updateOrder(User user, Long orderId, OrdersStatus status) {

        User findUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다."));
        Orders findOrder = ordersRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("오더을 찾을 수 없습니다."));
        // 주문 저장

        findUser.validateAndOrders(findOrder);
        findOrder.updateStatus(OrdersStatus.PAYMENTCOMPLETED);
        Orders savedOrder = ordersRepository.save(findOrder);
        return OrdersResponseDto.toDto(savedOrder);
    }

}
