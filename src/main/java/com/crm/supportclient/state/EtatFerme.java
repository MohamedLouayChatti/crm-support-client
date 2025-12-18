package com.crm.supportclient.state;

import com.crm.supportclient.model.Ticket;
import com.crm.supportclient.model.StatutTicket;

/** État Fermé - Ticket clôturé. */
public class EtatFerme implements TicketState {

    @Override
    public void assigner(Ticket ticket) {
        System.out.println("[État Fermé] Réouverture exceptionnelle du ticket #" + ticket.getId());
        ticket.setStatut(StatutTicket.OUVERT);
        ticket.setState(new EtatOuvert());
        ticket.setDateResolution(null);
        ticket.setSolution(null);
        ticket.notifyObservers();
    }

    @Override
    public void resoudre(Ticket ticket, String solution) {
        System.out.println("[État Fermé] Impossible de résoudre un ticket fermé.");
        throw new IllegalStateException("Un ticket fermé ne peut pas être résolu. Réouvrez-le d'abord.");
    }

    @Override
    public void fermer(Ticket ticket) {
        System.out.println("[État Fermé] Le ticket #" + ticket.getId() + " est déjà fermé.");
    }

    @Override
    public String getNomEtat() {
        return "Fermé";
    }
}
