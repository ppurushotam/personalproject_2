package com.ai.payment.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "of")
public class TicketPaymentStatusEvent {
    private int ticketNo;
    private double amount;
    private String paymentStatus;

}
