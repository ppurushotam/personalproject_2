package com.ai.payment.events;

import lombok.Data;

@Data
public class NewTicketEvent {
    private int ticketNo;
    private double amount;
}
