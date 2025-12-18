package com.crm.supportclient.observer;

import com.crm.supportclient.model.Ticket;
import java.util.UUID;

/** Observateur - Notifie l'agent de support. */
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
            ===========================================================
            NOTIFICATION SUPPORT
            ===========================================================
            Agent: %s <%s>
            ID Agent: %s
            
            Mise a jour du ticket #%d
            
            Titre: %s
            Description: %s
            Priorite: %s
            Statut actuel: %s
            Cree le: %s
            %s
            ===========================================================
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
                "Assigne a: " + ticket.getAssignedSupportId() : "Non assigne"
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
