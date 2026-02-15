# Sport Entreprise (JavaFX + MySQL)

Application JavaFX pour gérer des événements sportifs en entreprise :
- RH : création/gestion d’événements, annulation (soft delete), suivi
- Employé : consulter événements ouverts, s’inscrire, notifications & rappels
- Notifications : nouvel événement, confirmation d’inscription, rappel avant événement

## Stack
- Java 21
- JavaFX 21
- Maven
- MySQL (WAMP)

## Prérequis
- JDK 21 installé
- MySQL (WAMP) actif sur le port **3308**
- Base : `sport_entreprise`

## Installation (DB)
1. Créer la base `sport_entreprise`
2. Exécuter les scripts SQL :
   - `events`
   - `registrations`
   - `notifications`

## Lancer le projet
Dans IntelliJ :
- Ouvrir le projet
- Maven → `javafx:run`
ou via terminal IntelliJ :
```bash
mvn org.openjfx:javafx-maven-plugin:0.0.8:run
Structure

src/main/java/app/controllers : contrôleurs JavaFX

src/main/java/app/dao : accès DB (DAO)

src/main/java/app/model : modèles (User, Event, Notification…)

src/main/resources/views : fichiers FXML

src/main/java/app/router : navigation scènes

src/main/java/app/services : services (notifications, etc.)

Fonctionnalités clés

Soft delete événement : status = CANCELLED

Suppression interdite si inscriptions existantes

Rappels automatiques lors du login (fenêtre 24h)

Fenêtre Notifications (refresh auto + son + toast)

Auteur

Projet académique / mini-projet JavaFX
