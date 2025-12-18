package com.crm.supportclient.model;

import com.crm.supportclient.observer.TicketObserver;
import com.crm.supportclient.state.TicketState;
import com.crm.supportclient.state.EtatOuvert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entité Ticket - Utilise State et Observer patterns.
 */
public class Ticket {
    
    private int id;
    private String titre;
    private String description;
    private String priorite;
    private LocalDateTime dateCreation;
    private LocalDateTime dateResolution;
    private StatutTicket statut;
    private String solution;
    private UUID assignedSupportId;
    
    private TicketState state;
    private final List<TicketObserver> observers;
    private static int idCounter = 1;

    public Ticket() {
        this.id = idCounter++;
        this.dateCreation = LocalDateTime.now();
        this.statut = StatutTicket.OUVERT;
        this.state = new EtatOuvert();
        this.observers = new ArrayList<>();
    }

    public Ticket(String titre, String description, String priorite) {
        this();
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
    }

    /** Crée le ticket et notifie les observateurs. */
    public void creer() {
        System.out.println("[OK] Ticket #" + id + " cree avec succes.");
        System.out.println("   Titre: " + titre);
        System.out.println("   Priorite: " + priorite);
        System.out.println("   Etat initial: " + state.getNomEtat());
        notifyObservers();
    }

    /** Assigne le ticket à un agent. */
    public void assigner(UUID supportId) {
        this.assignedSupportId = supportId;
        state.assigner(this);
    }

    /** Résout le ticket. */
    public void resoudre(String solution) {
        state.resoudre(this, solution);
    }

    /** Ferme le ticket. */
    public void fermer() {
        state.fermer(this);
    }

    /** Ajoute un observateur. */
    public void attach(TicketObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("[+] Observateur ajoute au ticket #" + id);
        }
    }

    /** Retire un observateur. */
    public void detach(TicketObserver observer) {
        observers.remove(observer);
        System.out.println("[-] Observateur retire du ticket #" + id);
    }

    /** Notifie les observateurs. */
    public void notifyObservers() {
        System.out.println("\n[NOTIFY] Notification de " + observers.size() + " observateur(s)...\n");
        for (TicketObserver observer : observers) {
            observer.update(this);
        }
    }

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
