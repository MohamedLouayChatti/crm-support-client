# CRM - Module Support Client

## Description

Ce projet implemente le module **Support Client** d'un CRM (Customer Relationship Management) dans le cadre du cours d'Analyse et Conception (GL3).

Le module demontre l'application de deux **Design Patterns** majeurs :
- **State Pattern** : Gestion des etats du cycle de vie d'un ticket
- **Observer Pattern** : Systeme de notifications automatiques

## Architecture

```
src/main/java/com/crm/supportclient/
├── Main.java                          # Point d'entrée, démonstration
├── model/
│   ├── Ticket.java                    # Entité principale (Subject)
│   ├── ArticleBaseConnaissance.java   # Articles de la FAQ
│   └── StatutTicket.java              # Énumération des statuts
├── state/                             # STATE PATTERN
│   ├── TicketState.java               # Interface State
│   ├── EtatOuvert.java                # État initial
│   ├── EtatAssigne.java               # Ticket assigné
│   ├── EtatEnCours.java               # En traitement
│   ├── EtatResolu.java                # Résolu
│   └── EtatFerme.java                 # Fermé
├── observer/                          # OBSERVER PATTERN
│   ├── TicketObserver.java            # Interface Observer
│   ├── ClientNotification.java        # Notifie le client
│   └── SupportNotification.java       # Notifie l'agent
├── service/
│   └── PortailClient.java             # Façade pour les clients
└── external/
    ├── Client.java                    # Entité externe (Core)
    └── SupportClient.java             # Agent de support (Core)
```

## Design Patterns Implementes

### 1. State Pattern

**Probleme resolu** : Le comportement d'un ticket varie selon son etat (Ouvert, Assigne, En Cours, Resolu, Ferme). Sans le pattern State, le code serait pollue de conditions `if/else` ou `switch`.

**Solution** : Chaque etat est encapsule dans une classe qui implemente `TicketState`. Le ticket delegue les operations a son etat courant.

```
+----------+     +-------------+     +-----------+     +----------+     +---------+
|  Ouvert  | --> |   Assigne   | --> | En Cours  | --> |  Resolu  | --> |  Ferme  |
+----------+     +-------------+     +-----------+     +----------+     +---------+
     |                |                                     |
     +----------------+-------------------------------------+
                        (Reouverture possible)
```

### 2. Observer Pattern

**Probleme resolu** : Plusieurs parties prenantes (client, agent support) doivent etre informees des changements de statut d'un ticket, sans creer de couplage fort.

**Solution** : Le `Ticket` maintient une liste d'`TicketObserver`. Lors de chaque changement, tous les observateurs sont notifies automatiquement.

```
        +---------------------+
        |       Ticket        |
        |     (Subject)       |
        +----------+----------+
                   | notifyObservers()
          +--------+--------+
          v                 v
+------------------+ +------------------+
|ClientNotification| |SupportNotification|
|   (Observer)     | |    (Observer)     |
+------------------+ +------------------+
```

## Principes SOLID Respectes

| Principe | Application |
|----------|-------------|
| **SRP** | Chaque etat gere uniquement sa logique de transition |
| **OCP** | Nouveaux etats ajoutables sans modifier `Ticket` |
| **LSP** | Tous les `TicketState` sont interchangeables |
| **ISP** | Interface `TicketObserver` minimale (une seule methode) |
| **DIP** | `Ticket` depend des abstractions (`TicketState`, `TicketObserver`) |

## Execution

### Prerequis
- Java 21+ (upgraded from Java 17)
- Maven 3.8+

### Compiler et executer

```bash
# Compiler le projet
mvn clean compile

# Executer la demonstration console
mvn exec:java

# Executer l'interface web (http://localhost:8080)
mvn exec:java@run-web