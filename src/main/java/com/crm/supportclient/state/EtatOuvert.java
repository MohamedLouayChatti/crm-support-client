package com.crm.supportclient.state;

import com.crm.supportclient.model.Ticket;
import com.crm.supportclient.model.StatutTicket;

/** État Ouvert - Ticket non assigné. */
public class EtatOuvert implements TicketState {

    @Override
    public void assigner(Ticket ticket) {
        System.out.println("[État Ouvert] Assignation du ticket #" + ticket.getId());
        ticket.setStatut(StatutTicket.ASSIGNE);
        ticket.setState(new EtatAssigne());
        ticket.notifyObservers();
    }

    @Override
    public void resoudre(Ticket ticket, String solution) {
        System.out.println("[État Ouvert] Impossible de résoudre un ticket non assigné.");
        throw new IllegalStateException("Un ticket ouvert doit d'abord être assigné avant d'être résolu.");
    }

    @Override
    public void fermer(Ticket ticket) {
        System.out.println("[État Ouvert] Impossible de fermer un ticket non traité.");
        throw new IllegalStateException("Un ticket ouvert ne peut pas être fermé directement.");
    }

    @Override
    public String getNomEtat() {
        return "Ouvert";
    }
}
