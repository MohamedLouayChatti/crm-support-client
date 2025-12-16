package com.crm.supportclient.service;

import com.crm.supportclient.model.ArticleBaseConnaissance;
import com.crm.supportclient.model.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe PortailClient - Interface principale pour les clients.
 * 
 * Le portail client permet aux clients de:
 * - Consulter la base de connaissances
 * - Consulter leurs tickets
 * - Créer de nouveaux tickets
 */
public class PortailClient {
    
    // Stockage en mémoire (simulation de base de données)
    private final Map<Integer, Ticket> ticketsByClient;
    private final List<ArticleBaseConnaissance> baseConnaissance;

    public PortailClient() {
        this.ticketsByClient = new HashMap<>();
        this.baseConnaissance = new ArrayList<>();
    }

    /**
     * Consulte la base de connaissances.
     * 
     * @return Liste de tous les articles de la base de connaissances
     */
    public List<ArticleBaseConnaissance> consulterBaseConnaissance() {
        System.out.println("📚 Consultation de la base de connaissances...");
        System.out.println("   " + baseConnaissance.size() + " article(s) trouvé(s)");
        return new ArrayList<>(baseConnaissance);
    }

    /**
     * Recherche des articles par mot-clé.
     * 
     * @param motCle Le mot-clé à rechercher
     * @return Liste des articles correspondants
     */
    public List<ArticleBaseConnaissance> rechercherArticles(String motCle) {
        System.out.println("🔍 Recherche d'articles contenant: " + motCle);
        
        List<ArticleBaseConnaissance> resultats = baseConnaissance.stream()
            .filter(article -> 
                article.getTitre().toLowerCase().contains(motCle.toLowerCase()) ||
                article.getContenu().toLowerCase().contains(motCle.toLowerCase()))
            .toList();
        
        System.out.println("   " + resultats.size() + " résultat(s) trouvé(s)");
        return resultats;
    }

    /**
     * Consulte les tickets d'un client spécifique.
     * 
     * @param clientId L'identifiant du client
     * @return Liste des tickets du client
     */
    public List<Ticket> consulterTickets(int clientId) {
        System.out.println("📋 Consultation des tickets du client #" + clientId);
        
        List<Ticket> tickets = ticketsByClient.values().stream()
            .filter(ticket -> ticket.getId() == clientId) // Simplification
            .toList();
        
        System.out.println("   " + tickets.size() + " ticket(s) trouvé(s)");
        return new ArrayList<>(tickets);
    }

    /**
     * Ajoute un ticket au portail.
     * 
     * @param clientId L'identifiant du client
     * @param ticket Le ticket à ajouter
     */
    public void ajouterTicket(int clientId, Ticket ticket) {
        ticketsByClient.put(ticket.getId(), ticket);
        System.out.println("✅ Ticket #" + ticket.getId() + " ajouté pour le client #" + clientId);
    }

    /**
     * Ajoute un article à la base de connaissances.
     * 
     * @param article L'article à ajouter
     */
    public void ajouterArticle(ArticleBaseConnaissance article) {
        baseConnaissance.add(article);
        article.ajouter();
    }

    /**
     * Récupère un ticket par son ID.
     * 
     * @param ticketId L'identifiant du ticket
     * @return Le ticket ou null s'il n'existe pas
     */
    public Ticket getTicket(int ticketId) {
        return ticketsByClient.get(ticketId);
    }

    /**
     * Récupère tous les tickets.
     * 
     * @return Liste de tous les tickets
     */
    public List<Ticket> getTousLesTickets() {
        return new ArrayList<>(ticketsByClient.values());
    }
}
