package com.ai.payment.consumer;

import com.ai.payment.entities.Payment;
import com.ai.payment.events.NewTicketEvent;
import com.ai.payment.events.TicketPaymentStatusEvent;
import com.ai.payment.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class NewTicketEventConsumer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PaymentService paymentService;

    @KafkaListener(topics = {"ai-payment-topic"}, groupId = "newTicketGroup", autoStartup = "true")
    public void consumeNewTicketEvent(String ticketEvent) throws JsonProcessingException {
        TicketPaymentStatusEvent paymentStatusEvent = null;
        Payment payment = null;

        ObjectMapper objectMapper = new ObjectMapper();
        final NewTicketEvent newTicketEvent = objectMapper.readValue(ticketEvent, NewTicketEvent.class);

        if (newTicketEvent.getAmount() <= 0) {
            paymentStatusEvent = TicketPaymentStatusEvent.of().ticketNo(newTicketEvent.getTicketNo())
                    .amount(newTicketEvent.getAmount())
                    .paymentStatus("failed").build();

            payment = Payment.of().ticketId(newTicketEvent.getTicketNo())
                    .amount(newTicketEvent.getAmount())
                    .paymentDate(LocalDateTime.now())
                    .status("failed").build();
        } else {
            paymentStatusEvent = TicketPaymentStatusEvent.of().ticketNo(newTicketEvent.getTicketNo())
                    .amount(newTicketEvent.getAmount())
                    .paymentStatus("success").build();

            payment = Payment.of().ticketId(newTicketEvent.getTicketNo())
                    .amount(newTicketEvent.getAmount())
                    .paymentDate(LocalDateTime.now())
                    .status("success").build();
        }

        paymentService.savePayment(payment);
        String paymentStatus = objectMapper.writeValueAsString(paymentStatusEvent);
        kafkaTemplate.send("ai-ticket-topic", String.valueOf(payment.getId()), paymentStatus);
    }
}
