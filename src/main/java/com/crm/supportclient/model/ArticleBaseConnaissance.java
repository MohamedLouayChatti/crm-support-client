package com.crm.supportclient.model;

import java.time.LocalDateTime;

/**
 * Article de la base de connaissances (FAQ).
 */
public class ArticleBaseConnaissance {
    
    private int id;
    private String titre;
    private String contenu;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereModification;
    
    private static int idCounter = 1;

    public ArticleBaseConnaissance() {
        this.id = idCounter++;
        this.dateCreation = LocalDateTime.now();
        this.derniereModification = LocalDateTime.now();
    }

    public ArticleBaseConnaissance(String titre, String contenu) {
        this();
        this.titre = titre;
        this.contenu = contenu;
    }

    public void ajouter() {
        this.dateCreation = LocalDateTime.now();
        this.derniereModification = LocalDateTime.now();
        System.out.println("[+] Article #" + id + " ajoute: " + titre);
    }

    public void modifier() {
        this.derniereModification = LocalDateTime.now();
        System.out.println("[*] Article #" + id + " modifie: " + titre);
    }

    public void supprimer() {
        System.out.println("[-] Article #" + id + " supprime: " + titre);
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
