package com.Soo_Shinsa.service.Impl;

import com.Soo_Shinsa.constant.TossPayMethod;
import com.Soo_Shinsa.constant.TossPayStatus;

import com.Soo_Shinsa.dto.payment.PaymentApproveRequestDto;
import com.Soo_Shinsa.dto.payment.PaymentRequestDto;
import com.Soo_Shinsa.dto.payment.PaymentResponseDto;
import com.Soo_Shinsa.model.Orders;
import com.Soo_Shinsa.model.User;
import com.Soo_Shinsa.model.Payment;
import com.Soo_Shinsa.repository.OrdersRepository;
import com.Soo_Shinsa.repository.PaymentRepository;
import com.Soo_Shinsa.service.TossPaymentsService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class TossPaymentsServiceImpl implements TossPaymentsService {
    private final PaymentRepository paymentRepository;
    private final OrdersRepository ordersRepository;
    private final WebClient webClient;

    @Value("${toss.secret-key}")
    private String secretKey;


    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto requestDto, User user) {
        // 주문 및 사용자 조회
        Orders order = ordersRepository.findById(requestDto.getOrder())
                .orElseThrow(() -> new IllegalArgumentException("오더가 없습니다"));


        // Payment 엔티티 생성
        Payment payment = new Payment(
                requestDto.getPaymentKey(),
                requestDto.getOrderId(),
                order.getTotalPrice(),
                TossPayStatus.READY,
                TossPayMethod.Card,
                order,
                user
        );

        Payment savedPayment = paymentRepository.save(payment);

        String baseUrl = "https://api.tosspayments.com/v1";
        String secretKey = "test_sk_6bJXmgo28eBv06JyKg7e3LAnGKWx";

        String authHeader = "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", authHeader)
                .defaultHeader("Content-Type", "application/json")
                .build();

        String paymentKey = webClient.post()
                .uri("/payments")
                .bodyValue(Map.of(
                        "orderId", requestDto.getOrderId(),
                        "amount", order.getTotalPrice(),
                        "order", requestDto.getOrder() // 주문 이름을 동적으로 설정할 수도 있음
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("Payment Key: " + paymentKey);

        // 응답 DTO 생성 및 반환
        return PaymentResponseDto.toDto(savedPayment);

    }

    @Transactional
    public PaymentResponseDto approvePayment(PaymentApproveRequestDto requestDto, User user) {
        // 주문 조회
        Orders order = ordersRepository.findById(requestDto.getOrder())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + requestDto.getOrderId()));

        // 결제 데이터 검증

        Payment payment = paymentRepository.findByPaymentKey(requestDto.getPaymentKey());
//        if (payment == null || !payment.getUser().equals(user) || !payment.getOrder().equals(order)) {
//            throw new IllegalArgumentException("결제 데이터가 유효하지 않습니다.");
//        }
//        if(payment == null){
//            throw new IllegalArgumentException("payment가 null입니다.");
//        }
//        if(!payment.getUser().equals(user)){
//            throw new IllegalArgumentException("같은 유저가 아닙니다");
//        }
//        if(!payment.getOrder().equals(order)){
//            throw new IllegalArgumentException("같은 오더가 아닙니다");
//
//
//        // 결제 금액 검증
//        if (!payment.isAmountValid()) {
//            throw new IllegalArgumentException("결제 금액이 주문 금액과 일치하지 않습니다.");
//        }

        // 토스페이먼츠 API 호출
        try {
            // Basic Authentication Header 생성
            String authHeader = "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

            // WebClient로 POST 요청
            PaymentResponseDto response = webClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .headers(headers -> {
                        headers.add("Authorization", authHeader);
                        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    }) // Authorization 헤더 추가
                    .bodyValue(requestDto) // 요청 본문
                    .retrieve()
                    .bodyToMono(PaymentResponseDto.class) // 응답을 Mono로 변환
                    .block(); // 동기식 처리

            return response;

        } catch (WebClientResponseException e) {
            // 4xx 또는 5xx 에러 처리
            e.printStackTrace();
            System.err.println("Error Response: " + e.getResponseBodyAsString());
            throw new RuntimeException("결제 승인 중 오류 발생: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("결제 승인 요청 중 예외 발생", e);
        }
    }
}