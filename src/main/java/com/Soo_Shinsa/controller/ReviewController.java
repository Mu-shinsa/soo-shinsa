package com.Soo_Shinsa.controller;

import com.Soo_Shinsa.auth.UserDetailsImp;
import com.Soo_Shinsa.dto.ReviewRequestDto;
import com.Soo_Shinsa.dto.ReviewResponseDto;
import com.Soo_Shinsa.dto.ReviewUpdateDto;
import com.Soo_Shinsa.service.ReviewService;
import com.Soo_Shinsa.utils.UserUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/order-item/{orderItemId}")
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable Long orderItemId,
                                                          @Valid @RequestBody ReviewRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImp userDetails) {
        UserUtils.getUser(userDetails);
        ReviewResponseDto review = reviewService.createReview(orderItemId, requestDto);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(Long reviewId) {
        ReviewResponseDto review = reviewService.getReview(reviewId);
        return ResponseEntity.ok(review);
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewUpdateDto> updateReview(@PathVariable Long reviewId,
                                                        @Valid @RequestBody ReviewUpdateDto updateDto,
                                                        @AuthenticationPrincipal UserDetailsImp userDetails) {
        UserUtils.getUser(userDetails);
        ReviewUpdateDto review = reviewService.updateReview(reviewId, updateDto);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                             @AuthenticationPrincipal UserDetailsImp userDetails) {
        UserUtils.getUser(userDetails);
        reviewService.delete(reviewId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

