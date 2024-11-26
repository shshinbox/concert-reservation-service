package me.songha.concert.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class MockPaymentService {

    public PaymentStatus getPaymentStatus(Long reservationId) {
        double random = ThreadLocalRandom.current().nextDouble();

        if (random < 0.2) { // 취소: 20%
            log.info("Payment canceled. reservationId: {}", reservationId);
            return PaymentStatus.CANCELED;
        } else if (random < 0.6) { // 성공: 60%
            log.info("Payment successful. reservationId: {}", reservationId);
            return PaymentStatus.CONFIRMED;
        } else { // 결제 실패: 20%
            log.info("Payment rejected. reservationId: {}", reservationId);
            return PaymentStatus.REJECTED;
        }
    }
}