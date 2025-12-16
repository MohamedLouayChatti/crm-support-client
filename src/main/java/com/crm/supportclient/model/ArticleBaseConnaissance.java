package com.crm.supportclient.model;

import java.time.LocalDateTime;

/**
 * Classe ArticleBaseConnaissance - Représente un article de la base de connaissances.
 * 
 * La base de connaissances permet aux clients de trouver des solutions
 * à leurs problèmes courants sans avoir à créer un ticket.
 */
public class ArticleBaseConnaissance {
    
    private int id;
    private String titre;
    private String contenu;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereModification;
    
    // Compteur statique pour générer les IDs
    private static int idCounter = 1;

    /**
     * Constructeur par défaut.
     */
    public ArticleBaseConnaissance() {
        this.id = idCounter++;
        this.dateCreation = LocalDateTime.now();
        this.derniereModification = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres.
     */
    public ArticleBaseConnaissance(String titre, String contenu) {
        this();
        this.titre = titre;
        this.contenu = contenu;
    }

    // ===== MÉTHODES MÉTIER =====

    /**
     * Ajoute l'article à la base de connaissances.
     */
    public void ajouter() {
        this.dateCreation = LocalDateTime.now();
        this.derniereModification = LocalDateTime.now();
        System.out.println("[+] Article #" + id + " ajoute: " + titre);
    }

    /**
     * Modifie l'article existant.
     */
    public void modifier() {
        this.derniereModification = LocalDateTime.now();
        System.out.println("[*] Article #" + id + " modifie: " + titre);
    }

    /**
     * Supprime l'article de la base de connaissances.
     */
    public void supprimer() {
        System.out.println("[-] Article #" + id + " supprime: " + titre);
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

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDerniereModification() {
        return derniereModification;
    }

    public void setDerniereModification(LocalDateTime derniereModification) {
        this.derniereModification = derniereModification;
    }

    @Override
    public String toString() {
        return String.format(
            "Article{id=%d, titre='%s', créé=%s, modifié=%s}",
            id, titre, dateCreation, derniereModification
        );
    }
}
