package com.crm.supportclient.observer;

import com.crm.supportclient.model.Ticket;

/**
 * Interface Observer Pattern - Définit le contrat pour les observateurs de tickets.
 * 
 * Design Pattern: OBSERVER
 * Problème résolu: Permet de notifier automatiquement plusieurs parties prenantes
 * (clients, agents support) lors de changements d'état d'un ticket sans créer
 * de couplage fort entre le Ticket et ses observateurs.
 */
public interface TicketObserver {
    
    /**
     * Méthode appelée lorsque le ticket observé change d'état.
     * @param ticket Le ticket qui a été modifié
     */
    void update(Ticket ticket);
}
