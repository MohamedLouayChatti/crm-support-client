package com.crm.supportclient.model;

/** Statuts possibles d'un ticket. */
public enum StatutTicket {
    OUVERT("Ouvert"),
    ASSIGNE("Assigné"),
    EN_COURS("En cours"),
    RESOLU("Résolu"),
    FERME("Fermé");

    private final String libelle;

    StatutTicket(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
