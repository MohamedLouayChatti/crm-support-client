package com.crm.supportclient.observer;

import com.crm.supportclient.model.Ticket;

/** Observateur - Notifie le client par email. */
public class ClientNotification implements TicketObserver {
    
    private final int clientId;
    private final String clientEmail;

    public ClientNotification(int clientId, String clientEmail) {
        this.clientId = clientId;
        this.clientEmail = clientEmail;
    }

    @Override
    public void update(Ticket ticket) {
        String message = String.format(
            """
            ===========================================================
            NOTIFICATION CLIENT
            ===========================================================
            Destinataire: %s (Client #%d)
            
            Votre ticket #%d a ete mis a jour!
            
            Titre: %s
            Nouveau statut: %s
            Date de creation: %s
            %s
            ===========================================================
            """,
            clientEmail,
            clientId,
            ticket.getId(),
            ticket.getTitre(),
            ticket.getStatut().getLibelle(),
            ticket.getDateCreation(),
            ticket.getSolution() != null ? "Solution: " + ticket.getSolution() : ""
        );
        
        System.out.println(message);
        // En production: envoi réel d'email via service de messagerie
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientEmail() {
        return clientEmail;
    }
}
