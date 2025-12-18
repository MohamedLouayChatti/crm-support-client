package com.crm.supportclient.state;

import com.crm.supportclient.model.Ticket;
import com.crm.supportclient.model.StatutTicket;

/** État Assigné - Ticket attribué à un agent. */
public class EtatAssigne implements TicketState {

    @Override
    public void assigner(Ticket ticket) {
        System.out.println("[État Assigné] Réassignation du ticket #" + ticket.getId());
        // Le ticket reste dans l'état assigné mais change d'agent
        ticket.notifyObservers();
    }

    @Override
    public void resoudre(Ticket ticket, String solution) {
        System.out.println("[État Assigné] Passage en cours de traitement du ticket #" + ticket.getId());
        ticket.setStatut(StatutTicket.EN_COURS);
        ticket.setState(new EtatEnCours());
        ticket.notifyObservers();
        
        // Déléguer la résolution à l'état EnCours
        ticket.getState().resoudre(ticket, solution);
    }

    @Override
    public void fermer(Ticket ticket) {
        System.out.println("[État Assigné] Impossible de fermer un ticket non résolu.");
        throw new IllegalStateException("Un ticket assigné doit être résolu avant d'être fermé.");
    }

    @Override
    public String getNomEtat() {
        return "Assigné";
    }
}
