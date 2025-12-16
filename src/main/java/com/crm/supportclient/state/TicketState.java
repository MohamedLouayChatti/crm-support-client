package com.crm.supportclient.state;

import com.crm.supportclient.model.Ticket;

/**
 * Interface State Pattern - Définit les comportements possibles selon l'état du ticket.
 * 
 * Design Pattern: STATE
 * Problème résolu: Permet de modifier le comportement d'un ticket en fonction de son état
 * sans utiliser de conditions complexes (if/else ou switch).
 */
public interface TicketState {
    
    /**
     * Assigne le ticket à un agent de support.
     * @param ticket Le ticket à assigner
     */
    void assigner(Ticket ticket);
    
    /**
     * Résout le ticket avec une solution.
     * @param ticket Le ticket à résoudre
     * @param solution La solution apportée
     */
    void resoudre(Ticket ticket, String solution);
    
    /**
     * Ferme le ticket.
     * @param ticket Le ticket à fermer
     */
    void fermer(Ticket ticket);
    
    /**
     * Retourne le nom de l'état.
     * @return Le nom de l'état
     */
    String getNomEtat();
}
