package com.Soo_Shinsa.service.Impl;

import com.Soo_Shinsa.auth.UserDetailsImp;
import com.Soo_Shinsa.dto.CartItemResponseDto;
import com.Soo_Shinsa.entity.CartItem;
import com.Soo_Shinsa.entity.ProductOption;
import com.Soo_Shinsa.model.User;
import com.Soo_Shinsa.repository.CartItemRepository;
import com.Soo_Shinsa.repository.ProcductOptionRepository;
import com.Soo_Shinsa.repository.UserRepository;
import com.Soo_Shinsa.service.CartItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProcductOptionRepository procductOptionRepository;

    @Override
    public CartItemResponseDto create(Long optionId,Integer quantity,Long userId) {

        User user = checkUser(userId);
        Optional<ProductOption> findOption = procductOptionRepository.findById(optionId);

        CartItem cartItem = new CartItem(quantity,user,findOption.get());

        return CartItemResponseDto.toDto(cartItem);
    }

    @Override
    public CartItemResponseDto findById(Long cartId, Long userId) {

        checkUser(userId);
        CartItem savedCart = findByIdOrElseThrow(cartId);
        return CartItemResponseDto.toDto(savedCart);

    }

    @Override
    public List<CartItemResponseDto> findByAll(Long userId) {
        checkUser(userId);
        User findUser = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


        List<CartItem> allCartItem = cartItemRepository.findAllByUserUserId(findUser.getUserId());

        return allCartItem.stream().map(CartItemResponseDto::toDto).toList();
    }


    @Override
    public CartItemResponseDto update(Long cartId, Long userId,Integer quantity) {
        checkUser(userId);
        CartItem findCart = findByIdOrElseThrow(cartId);
        findCart.updateCartItem(quantity);

        CartItem saved = cartItemRepository.save(findCart);
        return CartItemResponseDto.toDto(saved);
    }

    @Override
    public void delete(Long cartId, Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImp userDetails = (UserDetailsImp) authentication.getPrincipal();
        User user = userDetails.getUser();

        User loginId = userRepository.findById(user.getUserId()).orElseThrow(() -> new EntityNotFoundException("해당 id값이 존재하지 않습니다."));;

        if(!loginId.getUserId().equals(userId)){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
        CartItem findCart = findByIdOrElseThrow(cartId);
        cartItemRepository.delete(findCart);

    }

    @Override
    public CartItem findByIdOrElseThrow(Long id) {
        return cartItemRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    private User checkUser(Long userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImp userDetails = (UserDetailsImp) authentication.getPrincipal();
        User user = userDetails.getUser();

        User loginId = userRepository.findById(user.getUserId()).orElseThrow(() -> new EntityNotFoundException("해당 id값이 존재하지 않습니다."));;

        if(!loginId.getUserId().equals(userId)){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
        return user;
    }


}
