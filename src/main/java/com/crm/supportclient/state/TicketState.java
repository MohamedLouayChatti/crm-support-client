package com.crm.supportclient.state;

import com.crm.supportclient.model.Ticket;

/** Interface State - Comportements selon l'état du ticket. */
public interface TicketState {
    void assigner(Ticket ticket);
    void resoudre(Ticket ticket, String solution);
    void fermer(Ticket ticket);
    String getNomEtat();
}
