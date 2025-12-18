package com.crm.supportclient.external;

/**
 * Client du CRM (entité externe du module Core).
 */
public class Client {
    
    private int id;
    private String nom;
    private String email;
    private String telephone;

    public Client() {
    }

    public Client(int id, String nom, String email) {
        this.id = id;
        this.nom = nom;
        this.email = email;
    }

    public Client(int id, String nom, String email, String telephone) {
        this(id, nom, email);
        this.telephone = telephone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return String.format("Client{id=%d, nom='%s', email='%s'}", id, nom, email);
    }
}
