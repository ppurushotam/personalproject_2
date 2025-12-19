package com.ai.payment.service;

import com.ai.payment.entities.Payment;
import com.ai.payment.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = false)
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
