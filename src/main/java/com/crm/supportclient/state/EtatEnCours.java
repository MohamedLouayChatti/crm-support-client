package com.crm.supportclient.state;

import com.crm.supportclient.model.Ticket;
import com.crm.supportclient.model.StatutTicket;

/**
 * État En Cours - Le ticket est en cours de traitement par l'agent.
 * 
 * Transitions possibles:
 * - Peut être résolu → EtatResolu
 * - Peut être réassigné → EtatAssigne
 * - Ne peut pas être fermé directement
 */
public class EtatEnCours implements TicketState {

    @Override
    public void assigner(Ticket ticket) {
        System.out.println("[État En Cours] Réassignation du ticket #" + ticket.getId() + " - Retour à l'état Assigné");
        ticket.setStatut(StatutTicket.ASSIGNE);
        ticket.setState(new EtatAssigne());
        ticket.notifyObservers();
    }

    @Override
    public void resoudre(Ticket ticket, String solution) {
        System.out.println("[État En Cours] Résolution du ticket #" + ticket.getId());
        System.out.println("Solution apportée: " + solution);
        ticket.setSolution(solution);
        ticket.setStatut(StatutTicket.RESOLU);
        ticket.setState(new EtatResolu());
        ticket.setDateResolution(java.time.LocalDateTime.now());
        ticket.notifyObservers();
    }

    @Override
    public void fermer(Ticket ticket) {
        System.out.println("[État En Cours] Impossible de fermer un ticket non résolu.");
        throw new IllegalStateException("Un ticket en cours doit être résolu avant d'être fermé.");
    }

    @Override
    public String getNomEtat() {
        return "En Cours";
    }
}
