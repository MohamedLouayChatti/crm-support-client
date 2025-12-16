package com.crm.supportclient.observer;

import com.crm.supportclient.model.Ticket;
import java.util.UUID;

/**
 * Observateur concret - Notification pour l'agent de support.
 * 
 * Cette classe implémente l'interface TicketObserver pour envoyer
 * des notifications à l'agent de support assigné au ticket.
 */
public class SupportNotification implements TicketObserver {
    
    private final UUID supportId;
    private final String supportName;
    private final String supportEmail;

    public SupportNotification(UUID supportId, String supportName, String supportEmail) {
        this.supportId = supportId;
        this.supportName = supportName;
        this.supportEmail = supportEmail;
    }

    @Override
    public void update(Ticket ticket) {
        String message = String.format(
            """
            ═══════════════════════════════════════════════════════
            🔔 NOTIFICATION SUPPORT
            ═══════════════════════════════════════════════════════
            Agent: %s <%s>
            ID Agent: %s
            
            Mise à jour du ticket #%d
            
            📋 Titre: %s
            📝 Description: %s
            🎯 Priorité: %s
            📊 Statut actuel: %s
            📅 Créé le: %s
            %s
            ═══════════════════════════════════════════════════════
            """,
            supportName,
            supportEmail,
            supportId,
            ticket.getId(),
            ticket.getTitre(),
            ticket.getDescription(),
            ticket.getPriorite(),
            ticket.getStatut().getLibelle(),
            ticket.getDateCreation(),
            ticket.getAssignedSupportId() != null ? 
                "👤 Assigné à: " + ticket.getAssignedSupportId() : "⚠️ Non assigné"
        );
        
        System.out.println(message);
        // En production: envoi réel de notification via système interne
    }

    public UUID getSupportId() {
        return supportId;
    }

    public String getSupportName() {
        return supportName;
    }

    public String getSupportEmail() {
        return supportEmail;
    }
}
