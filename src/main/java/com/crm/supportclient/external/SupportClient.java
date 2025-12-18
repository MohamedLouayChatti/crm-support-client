package com.crm.supportclient.external;

import java.util.UUID;

/**
 * Agent de support (entité externe du module Core).
 */
public class SupportClient {
    
    private UUID id;
    private String nom;
    private String email;
    private String specialite;
    private boolean disponible;

    public SupportClient() {
        this.id = UUID.randomUUID();
        this.disponible = true;
    }

    public SupportClient(String nom, String email) {
        this();
        this.nom = nom;
        this.email = email;
    }

    public SupportClient(String nom, String email, String specialite) {
        this(nom, email);
        this.specialite = specialite;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return String.format(
            "SupportClient{id=%s, nom='%s', email='%s', specialite='%s', disponible=%s}",
            id, nom, email, specialite, disponible
        );
    }
}
