package com.crm.supportclient.model;

import com.crm.supportclient.observer.TicketObserver;
import com.crm.supportclient.state.TicketState;
import com.crm.supportclient.state.EtatOuvert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Classe Ticket - Entité principale du module Support Client.
 * 
 * Cette classe implémente deux Design Patterns:
 * 
 * 1. STATE PATTERN: Le comportement du ticket change selon son état (state).
 *    Les méthodes assigner(), resoudre(), fermer() délèguent au state courant.
 * 
 * 2. OBSERVER PATTERN: Le ticket maintient une liste d'observateurs (observers)
 *    qui sont notifiés automatiquement lors de changements d'état.
 * 
 * Principes SOLID respectés:
 * - SRP: La logique d'état est déléguée aux classes State
 * - OCP: Nouveaux états ajoutables sans modifier Ticket
 * - LSP: Tous les états sont substituables via l'interface TicketState
 * - DIP: Ticket dépend de l'abstraction TicketState, pas des implémentations
 */
public class Ticket {
    
    // Attributs de base
    private int id;
    private String titre;
    private String description;
    private String priorite;
    private LocalDateTime dateCreation;
    private LocalDateTime dateResolution;
    private StatutTicket statut;
    private String solution;
    private UUID assignedSupportId;
    
    // State Pattern
    private TicketState state;
    
    // Observer Pattern
    private final List<TicketObserver> observers;
    
    // Compteur statique pour générer les IDs
    private static int idCounter = 1;

    /**
     * Constructeur par défaut - Crée un ticket à l'état initial (Ouvert).
     */
    public Ticket() {
        this.id = idCounter++;
        this.dateCreation = LocalDateTime.now();
        this.statut = StatutTicket.OUVERT;
        this.state = new EtatOuvert();
        this.observers = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     */
    public Ticket(String titre, String description, String priorite) {
        this();
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
    }

    // ===== MÉTHODES MÉTIER (délèguent au State) =====

    /**
     * Crée et initialise le ticket.
     */
    public void creer() {
        System.out.println("✅ Ticket #" + id + " créé avec succès.");
        System.out.println("   Titre: " + titre);
        System.out.println("   Priorité: " + priorite);
        System.out.println("   État initial: " + state.getNomEtat());
        notifyObservers();
    }

    /**
     * Assigne le ticket à un agent de support.
     * Délègue le comportement au state courant.
     * 
     * @param supportId L'identifiant de l'agent de support
     */
    public void assigner(UUID supportId) {
        this.assignedSupportId = supportId;
        state.assigner(this);
    }

    /**
     * Résout le ticket avec une solution.
     * Délègue le comportement au state courant.
     * 
     * @param solution La solution apportée au problème
     */
    public void resoudre(String solution) {
        state.resoudre(this, solution);
    }

    /**
     * Ferme le ticket.
     * Délègue le comportement au state courant.
     */
    public void fermer() {
        state.fermer(this);
    }

    // ===== OBSERVER PATTERN METHODS =====

    /**
     * Attache un observateur au ticket.
     * 
     * @param observer L'observateur à ajouter
     */
    public void attach(TicketObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("📎 Observateur ajouté au ticket #" + id);
        }
    }

    /**
     * Détache un observateur du ticket.
     * 
     * @param observer L'observateur à retirer
     */
    public void detach(TicketObserver observer) {
        observers.remove(observer);
        System.out.println("📎 Observateur retiré du ticket #" + id);
    }

    /**
     * Notifie tous les observateurs d'un changement.
     */
    public void notifyObservers() {
        System.out.println("\n🔔 Notification de " + observers.size() + " observateur(s)...\n");
        for (TicketObserver observer : observers) {
            observer.update(this);
        }
    }

    // ===== GETTERS & SETTERS =====

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(LocalDateTime dateResolution) {
        this.dateResolution = dateResolution;
    }

    public StatutTicket getStatut() {
        return statut;
    }

    public void setStatut(StatutTicket statut) {
        this.statut = statut;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public UUID getAssignedSupportId() {
        return assignedSupportId;
    }

    public void setAssignedSupportId(UUID assignedSupportId) {
        this.assignedSupportId = assignedSupportId;
    }

    public List<TicketObserver> getObservers() {
        return new ArrayList<>(observers);
    }

    @Override
    public String toString() {
        return String.format(
            "Ticket{id=%d, titre='%s', priorite='%s', statut=%s, état=%s}",
            id, titre, priorite, statut, state.getNomEtat()
        );
    }
}
