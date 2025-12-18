package com.crm.supportclient.observer;

import com.crm.supportclient.model.Ticket;

/** Interface Observer pour les notifications de ticket. */
public interface TicketObserver {
    void update(Ticket ticket);
}
