package com.crm.supportclient;

import com.crm.supportclient.external.Client;
import com.crm.supportclient.external.SupportClient;
import com.crm.supportclient.model.ArticleBaseConnaissance;
import com.crm.supportclient.model.Ticket;
import com.crm.supportclient.observer.ClientNotification;
import com.crm.supportclient.observer.SupportNotification;
import com.crm.supportclient.service.PortailClient;

/**
 * Classe principale - Démonstration du module Support Client.
 * 
 * Ce programme démontre l'utilisation des Design Patterns:
 * 1. STATE PATTERN: Gestion des états du ticket (Ouvert → Assigné → EnCours → Résolu → Fermé)
 * 2. OBSERVER PATTERN: Notification automatique des clients et agents lors de changements
 * 
 * Principes SOLID illustrés:
 * - SRP (Single Responsibility): Chaque classe a une responsabilité unique
 * - OCP (Open/Closed): Les états sont extensibles sans modifier Ticket
 * - LSP (Liskov Substitution): Les états sont interchangeables
 * - DIP (Dependency Inversion): Dépendance envers les abstractions (interfaces)
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════════╗
            ║           CRM - MODULE SUPPORT CLIENT                        ║
            ║           Démonstration des Design Patterns                  ║
            ╚══════════════════════════════════════════════════════════════╝
            """);

        // ===== INITIALISATION =====
        
        // Créer les acteurs
        Client client = new Client(1, "Jean Dupont", "jean.dupont@email.com");
        SupportClient agentSupport = new SupportClient("Marie Martin", "marie.martin@crm.com", "Technique");
        
        System.out.println("👤 Client créé: " + client);
        System.out.println("👨‍💼 Agent Support créé: " + agentSupport);
        System.out.println();

        // Créer le portail client
        PortailClient portail = new PortailClient();

        // ===== DÉMONSTRATION BASE DE CONNAISSANCES =====
        
        System.out.println("""
            ────────────────────────────────────────────────────────────────
            📚 DÉMONSTRATION: BASE DE CONNAISSANCES
            ────────────────────────────────────────────────────────────────
            """);

        ArticleBaseConnaissance article1 = new ArticleBaseConnaissance(
            "Comment réinitialiser mon mot de passe?",
            "Pour réinitialiser votre mot de passe, cliquez sur 'Mot de passe oublié'..."
        );
        
        ArticleBaseConnaissance article2 = new ArticleBaseConnaissance(
            "Guide de démarrage rapide",
            "Bienvenue! Ce guide vous aidera à prendre en main notre solution..."
        );

        portail.ajouterArticle(article1);
        portail.ajouterArticle(article2);
        
        System.out.println();
        portail.consulterBaseConnaissance();
        System.out.println();

        // ===== DÉMONSTRATION STATE + OBSERVER PATTERNS =====
        
        System.out.println("""
            ────────────────────────────────────────────────────────────────
            🎫 DÉMONSTRATION: CYCLE DE VIE D'UN TICKET
            (State Pattern + Observer Pattern)
            ────────────────────────────────────────────────────────────────
            """);

        // 1. Créer un ticket
        System.out.println("▶ ÉTAPE 1: Création du ticket");
        System.out.println("─".repeat(50));
        
        Ticket ticket = new Ticket(
            "Problème de connexion",
            "Je n'arrive plus à me connecter depuis ce matin",
            "Haute"
        );

        // 2. Attacher les observateurs (Observer Pattern)
        System.out.println("\n▶ ÉTAPE 2: Attachement des observateurs");
        System.out.println("─".repeat(50));
        
        ClientNotification clientNotif = new ClientNotification(
            client.getId(), 
            client.getEmail()
        );
        
        SupportNotification supportNotif = new SupportNotification(
            agentSupport.getId(),
            agentSupport.getNom(),
            agentSupport.getEmail()
        );

        ticket.attach(clientNotif);
        ticket.attach(supportNotif);

        // 3. Créer le ticket (notifie les observateurs)
        System.out.println("\n▶ ÉTAPE 3: Enregistrement du ticket");
        System.out.println("─".repeat(50));
        ticket.creer();
        portail.ajouterTicket(client.getId(), ticket);

        // 4. Assigner le ticket (State: Ouvert → Assigné)
        System.out.println("\n▶ ÉTAPE 4: Assignation du ticket (État: Ouvert → Assigné)");
        System.out.println("─".repeat(50));
        ticket.assigner(agentSupport.getId());

        // 5. Résoudre le ticket (State: Assigné → EnCours → Résolu)
        System.out.println("\n▶ ÉTAPE 5: Résolution du ticket (État: Assigné → En Cours → Résolu)");
        System.out.println("─".repeat(50));
        ticket.resoudre("Le problème était lié à un cache navigateur. Videz le cache et reconnectez-vous.");

        // 6. Fermer le ticket (State: Résolu → Fermé)
        System.out.println("\n▶ ÉTAPE 6: Fermeture du ticket (État: Résolu → Fermé)");
        System.out.println("─".repeat(50));
        ticket.fermer();

        // ===== DÉMONSTRATION DES TRANSITIONS INVALIDES =====
        
        System.out.println("""
            
            ────────────────────────────────────────────────────────────────
            ⚠️ DÉMONSTRATION: TRANSITIONS D'ÉTAT INVALIDES
            ────────────────────────────────────────────────────────────────
            """);

        Ticket ticket2 = new Ticket(
            "Autre problème",
            "Description du problème",
            "Basse"
        );

        System.out.println("État actuel du ticket #" + ticket2.getId() + ": " + ticket2.getState().getNomEtat());
        
        try {
            System.out.println("\n❌ Tentative de fermer un ticket ouvert...");
            ticket2.fermer();
        } catch (IllegalStateException e) {
            System.out.println("   Exception capturée: " + e.getMessage());
        }

        try {
            System.out.println("\n❌ Tentative de résoudre un ticket non assigné...");
            ticket2.resoudre("Solution impossible");
        } catch (IllegalStateException e) {
            System.out.println("   Exception capturée: " + e.getMessage());
        }

        // ===== RÉSUMÉ =====
        
        System.out.println("""
            
            ════════════════════════════════════════════════════════════════
            📊 RÉSUMÉ DE LA DÉMONSTRATION
            ════════════════════════════════════════════════════════════════
            
            ✅ STATE PATTERN implémenté:
               - 5 états: Ouvert, Assigné, EnCours, Résolu, Fermé
               - Transitions contrôlées par les états eux-mêmes
               - Impossible d'effectuer des actions invalides
            
            ✅ OBSERVER PATTERN implémenté:
               - 2 types d'observateurs: ClientNotification, SupportNotification
               - Notifications automatiques à chaque changement d'état
               - Découplage entre Ticket et système de notification
            
            ✅ Principes SOLID respectés:
               - SRP: Chaque état gère sa propre logique de transition
               - OCP: Nouveaux états ajoutables sans modifier Ticket
               - LSP: Tous les TicketState sont substituables
               - DIP: Ticket dépend de l'interface TicketState
            
            ════════════════════════════════════════════════════════════════
            """);
    }
}
