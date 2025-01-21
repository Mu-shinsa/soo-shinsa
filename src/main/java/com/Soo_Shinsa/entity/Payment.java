package com.Soo_Shinsa.entity;

import com.Soo_Shinsa.constant.TossPayMethod;
import com.Soo_Shinsa.constant.TossPayStatus;
import com.Soo_Shinsa.model.BaseTimeEntity;
import com.Soo_Shinsa.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "payments")
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Value("${toss.secret-key}")
    private String paymentKey; // 토스페이먼츠에서 제공하는 고유 결제 키

    @Column(nullable = false)
    private BigDecimal amount; // 결제 금액

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TossPayStatus status; // 결제 상태 (READY, DONE, CANCELLED 등)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TossPayMethod method; // 결제 수단 (CARD)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", nullable = false)
    private Orders order; // Orders 엔티티와의 1:1 연관 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private User user; // User 엔티티와의 다대일 연관 관계

    public Payment(String paymentKey, BigDecimal amount, TossPayStatus status, TossPayMethod method, Orders order, User user) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.status = status;
        this.method = method;
        this.order = order;
        this.user = user;
    }


    public boolean isAmountValid() {
        return this.amount != null && this.order != null && this.amount.compareTo(order.getTotalPrice()) == 0;
    }

    public void updateStatus(TossPayStatus status) {
        if (this.status == TossPayStatus.READY) {
            this.status = TossPayStatus.DONE;
        }
    }
}