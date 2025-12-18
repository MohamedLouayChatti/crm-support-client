package com.crm.supportclient.service;

import com.crm.supportclient.model.ArticleBaseConnaissance;
import com.crm.supportclient.model.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Portail client - Gestion des tickets et base de connaissances. */
public class PortailClient {
    
    private final Map<Integer, Ticket> ticketsByClient;
    private final List<ArticleBaseConnaissance> baseConnaissance;

    public PortailClient() {
        this.ticketsByClient = new HashMap<>();
        this.baseConnaissance = new ArrayList<>();
    }

    public List<ArticleBaseConnaissance> consulterBaseConnaissance() {
        System.out.println("[KB] Consultation de la base de connaissances...");
        System.out.println("   " + baseConnaissance.size() + " article(s) trouve(s)");
        return new ArrayList<>(baseConnaissance);
    }

    public List<ArticleBaseConnaissance> rechercherArticles(String motCle) {
        System.out.println("[SEARCH] Recherche d'articles contenant: " + motCle);
        
        List<ArticleBaseConnaissance> resultats = baseConnaissance.stream()
            .filter(article -> 
                article.getTitre().toLowerCase().contains(motCle.toLowerCase()) ||
                article.getContenu().toLowerCase().contains(motCle.toLowerCase()))
            .toList();
        
        System.out.println("   " + resultats.size() + " résultat(s) trouvé(s)");
        return resultats;
    }

    public List<Ticket> consulterTickets(int clientId) {
        System.out.println("[TICKETS] Consultation des tickets du client #" + clientId);
        
        List<Ticket> tickets = ticketsByClient.values().stream()
            .filter(ticket -> ticket.getId() == clientId) // Simplification
            .toList();
        
        System.out.println("   " + tickets.size() + " ticket(s) trouvé(s)");
        return new ArrayList<>(tickets);
    }

    public void ajouterTicket(int clientId, Ticket ticket) {
        ticketsByClient.put(ticket.getId(), ticket);
        System.out.println("[OK] Ticket #" + ticket.getId() + " ajoute pour le client #" + clientId);
    }

    public void ajouterArticle(ArticleBaseConnaissance article) {
        baseConnaissance.add(article);
        article.ajouter();
    }

    public Ticket getTicket(int ticketId) {
        return ticketsByClient.get(ticketId);
    }

    public List<Ticket> getTousLesTickets() {
        return new ArrayList<>(ticketsByClient.values());
    }
}
