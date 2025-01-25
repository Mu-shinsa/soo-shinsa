package com.Soo_Shinsa.order;



import com.Soo_Shinsa.order.dto.OrderItemRequestDto;
import com.Soo_Shinsa.order.dto.OrderItemResponseDto;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository ;
    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    //오더 아이템 생성
    @Transactional
    public OrderItemResponseDto createOrderItem(OrderItemRequestDto requestDto,User user) {

        User findUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다."));
        // 주문 조회
        Orders order = ordersRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다.: " + requestDto.getOrderId()));

        checkUserAndOrders(order,findUser.getUserId());
        // 상품 조회
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다.: " + requestDto.getProductId()));
        // OrderItem 생성 및 저장
        OrderItem orderItem = new OrderItem(
                requestDto.getQuantity(),
                order,
                product
        );

        // OrderItem을 Order에 추가
        order.addOrderItem(orderItem);

        // OrderItem 저장
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // 변경된 Order도 저장 (CascadeType.ALL로 인해 필요하지 않을 수도 있음)
        ordersRepository.save(order);

        return OrderItemResponseDto.toDto(savedOrderItem);
    }
    //오더 아이템 찾아오고 dto로 변환
    @Transactional(readOnly = true)
    @Override
    public OrderItemResponseDto findById(Long orderItemsId,User user) {
        //오더 아이템을 찾아옴
        User findUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다."));
        OrderItem findOrderItem = findByIdOrElseThrow(orderItemsId);

        checkUserAndOrderItem(findOrderItem,findUser.getUserId());
        //dto로 변환
        return OrderItemResponseDto.toDto(findOrderItem);
    }


    //유저 오더아이템들을 찾아옴
    @Transactional(readOnly = true)
    @Override
    public Page<OrderItemResponseDto> findByAll(User user, Pageable pageable) {

        //회원의 모든 아이템 오더를 리스트르 받아옴
        Page<OrderItem> orderItems = orderItemRepository.findAllByUserIdWithFetchJoin(user.getUserId(),pageable);
        //dto로 변환
//        return orderItems.stream().map(OrderItemResponseDto::toDto).toList();
        return orderItems.map(OrderItemResponseDto::toDto);
    }
    //오더 아이템 수정
    @Transactional
    @Override
    public OrderItemResponseDto update(Long orderItemsId, Integer quantity,User user) {
        User findUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다."));
        //오더 아이템을 찾아옴
        OrderItem findOrder = findByIdOrElseThrow(orderItemsId);


        checkUserAndOrderItem(findOrder,findUser.getUserId());
        //찾아옴 오더아이템 수량을 변경
        findOrder.updateOrderItem(quantity);
        OrderItem save = orderItemRepository.save(findOrder);
        //dto로 변환
        return OrderItemResponseDto.toDto(save);
    }
    //오더 아이템 삭제
    @Override
    @Transactional
    public OrderItemResponseDto delete(Long orderItemsId,User user) {
        User findUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다."));
        // OrderItem 조회
        OrderItem find = findByIdOrElseThrow(orderItemsId);

        checkUserAndOrderItem(find,user.getUserId());
        // Orders 조회
        Orders order = ordersRepository.findById(find.getOrder().getId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다"));
        // OrderItem 삭제
        order.removeOrderItem(find); // 연관 관계에서 제거
        ordersRepository.delete(order);// Order 저장 (OrderItem 자동 삭제)
        //dto 변환
        return OrderItemResponseDto.toDto(find);

    }
    //오더 아이템을 찾아옴
    @Transactional(readOnly = true)
    @Override
    public OrderItem findByIdOrElseThrow(Long id) {
        return orderItemRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private static void checkUserAndOrders(Orders orders,Long userId) {
        if (!orders.getUser().getUserId().equals(userId)) {
            throw new SecurityException("수정 또는 삭제할 권한이 없습니다.");
        }
    }
    private static void checkUserAndOrderItem(OrderItem orderItem,Long userId) {
        if (!orderItem.getOrder().getUser().getUserId().equals(userId)) {
            throw new SecurityException("수정 또는 삭제할 권한이 없습니다.");
        }
    }
}
