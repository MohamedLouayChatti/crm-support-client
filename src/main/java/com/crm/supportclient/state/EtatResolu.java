package com.crm.supportclient.state;

import com.crm.supportclient.model.Ticket;
import com.crm.supportclient.model.StatutTicket;

/**
 * État Résolu - Le ticket a été résolu avec une solution.
 * 
 * Transitions possibles:
 * - Peut être fermé → EtatFerme
 * - Peut être réouvert (réassigné) si le client n'est pas satisfait → EtatAssigne
 */
public class EtatResolu implements TicketState {

    @Override
    public void assigner(Ticket ticket) {
        System.out.println("[État Résolu] Réouverture du ticket #" + ticket.getId() + " - Retour à l'état Assigné");
        ticket.setStatut(StatutTicket.ASSIGNE);
        ticket.setState(new EtatAssigne());
        ticket.setDateResolution(null);
        ticket.setSolution(null);
        ticket.notifyObservers();
    }

    @Override
    public void resoudre(Ticket ticket, String solution) {
        System.out.println("[État Résolu] Mise à jour de la solution du ticket #" + ticket.getId());
        ticket.setSolution(solution);
        ticket.setDateResolution(java.time.LocalDateTime.now());
        ticket.notifyObservers();
    }

    @Override
    public void fermer(Ticket ticket) {
        System.out.println("[État Résolu] Fermeture du ticket #" + ticket.getId());
        ticket.setStatut(StatutTicket.FERME);
        ticket.setState(new EtatFerme());
        ticket.notifyObservers();
    }

    @Override
    public String getNomEtat() {
        return "Résolu";
    }
}
