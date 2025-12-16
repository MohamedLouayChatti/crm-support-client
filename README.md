# CRM - Module Support Client

## 📋 Description

Ce projet implémente le module **Support Client** d'un CRM (Customer Relationship Management) dans le cadre du cours d'Analyse et Conception (GL3).

Le module démontre l'application de deux **Design Patterns** majeurs :
- **State Pattern** : Gestion des états du cycle de vie d'un ticket
- **Observer Pattern** : Système de notifications automatiques

## 🏗️ Architecture

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

## 🎯 Design Patterns Implémentés

### 1. State Pattern

**Problème résolu** : Le comportement d'un ticket varie selon son état (Ouvert, Assigné, En Cours, Résolu, Fermé). Sans le pattern State, le code serait pollué de conditions `if/else` ou `switch`.

**Solution** : Chaque état est encapsulé dans une classe qui implémente `TicketState`. Le ticket délègue les opérations à son état courant.

```
┌─────────┐     ┌─────────────┐     ┌───────────┐     ┌──────────┐     ┌─────────┐
│ Ouvert  │ ──► │   Assigné   │ ──► │ En Cours  │ ──► │  Résolu  │ ──► │  Fermé  │
└─────────┘     └─────────────┘     └───────────┘     └──────────┘     └─────────┘
     │                │                                     │
     └────────────────┴─────────────────────────────────────┘
                        (Réouverture possible)
```

### 2. Observer Pattern

**Problème résolu** : Plusieurs parties prenantes (client, agent support) doivent être informées des changements de statut d'un ticket, sans créer de couplage fort.

**Solution** : Le `Ticket` maintient une liste d'`TicketObserver`. Lors de chaque changement, tous les observateurs sont notifiés automatiquement.

```
        ┌─────────────────────┐
        │       Ticket        │
        │     (Subject)       │
        └──────────┬──────────┘
                   │ notifyObservers()
          ┌────────┴────────┐
          ▼                 ▼
┌──────────────────┐ ┌──────────────────┐
│ClientNotification│ │SupportNotification│
│   (Observer)     │ │    (Observer)     │
└──────────────────┘ └──────────────────┘
```

## ✅ Principes SOLID Respectés

| Principe | Application |
|----------|-------------|
| **SRP** | Chaque état gère uniquement sa logique de transition |
| **OCP** | Nouveaux états ajoutables sans modifier `Ticket` |
| **LSP** | Tous les `TicketState` sont interchangeables |
| **ISP** | Interface `TicketObserver` minimale (une seule méthode) |
| **DIP** | `Ticket` dépend des abstractions (`TicketState`, `TicketObserver`) |

## 🚀 Exécution

### Prérequis
- Java 17+
- Maven 3.8+

### Compiler et exécuter

```bash
# Compiler le projet
mvn clean compile

# Exécuter la démonstration
mvn exec:java

# Ou créer le JAR et l'exécuter
mvn package
java -jar target/support-client-1.0.0.jar
```

## 📊 Diagramme de Classes (PlantUML)

Le diagramme de classes complet est disponible dans le fichier `diagrams/support-client.puml`.

## 👥 Auteur

Projet réalisé dans le cadre du cours **Analyse et Conception** - GL3

## 📄 Licence

Projet académique - Usage éducatif uniquement
