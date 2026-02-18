Sport Entreprise

Application JavaFX de gestion d’événements sportifs en entreprise

1. Présentation générale

Sport Entreprise est une application desktop développée en Java 21 avec JavaFX et MySQL.
Elle permet de gérer l’organisation et la participation aux événements sportifs internes d’une entreprise.

L’application distingue deux types d’utilisateurs :

RH : gestion et supervision des événements

Employés : consultation et inscription aux événements

Le système intègre également un module de notifications internes et une gestion sécurisée des comptes utilisateurs.

2. Objectifs du projet

Mettre en place une application JavaFX complète avec architecture structurée

Implémenter une séparation claire entre interface, logique métier et accès aux données

Gérer une base de données relationnelle MySQL

Sécuriser l’authentification via hashage BCrypt

Automatiser certaines règles métiers (capacité, statuts, rappels)

3. Fonctionnalités principales
3.1 Gestion des utilisateurs

Authentification :

Connexion par email et mot de passe

Vérification sécurisée via BCrypt

Gestion des rôles (RH / Employé)

Inscription :

Création de compte employé

Validation des champs

Vérification de la force du mot de passe

Unicité de l’email

Gestion des rôles :

RH : accès à la gestion complète des événements

Employé : accès à la consultation et inscription

3.2 Gestion des événements (RH)

Création d’événement :

Sport

Date

Lieu

Capacité maximale

Lien optionnel avec un objectif

Gestion des statuts :

PLANNED

OPEN

CLOSED

CANCELLED

FINISHED

Soft delete :

Suppression logique via changement de statut en CANCELLED

Suppression interdite si des inscriptions existent

Fermeture automatique :

Si la capacité maximale est atteinte, l’événement passe automatiquement en statut CLOSED

3.3 Gestion des inscriptions (Employé)

Consultation :

Affichage uniquement des événements en statut OPEN

Tri par date croissante

Inscription :

Vérification de la capacité

Interdiction de double inscription

Mise à jour automatique du statut si complet

Annulation :

Modification du statut d’inscription

Pas de suppression physique des données

3.4 Système de notifications

Types de notifications :

Nouvel événement

Confirmation d’inscription

Rappel avant événement

Fonctionnalités :

Compteur de notifications non lues

Fenêtre dédiée aux notifications

Marquage comme lu

Génération automatique des rappels lors de la connexion

3.5 Interface utilisateur

Interface JavaFX structurée

Dashboards différenciés selon rôle

Header commun avec logo et profil utilisateur

Avatar dynamique basé sur les initiales

Menu profil (déconnexion, changement de mot de passe)

Gestion automatique des tailles de fenêtres selon écran

4. Architecture technique

L’application suit une architecture inspirée du modèle MVC (Model – View – Controller) avec séparation claire des responsabilités.

4.1 Structure du projet
app/
 ├── MainApp.java
 ├── router/
 ├── controllers/
 ├── dao/
 ├── model/
 ├── services/
 ├── session/
resources/
 └── views/

4.2 Description des composants

MainApp
Point d’entrée de l’application. Initialise JavaFX et charge l’écran de connexion.

Router
SceneRouter gère la navigation entre les écrans et l’ouverture de nouvelles fenêtres.

Controllers
Chaque écran FXML possède un contrôleur dédié :

LoginController

RegisterController

RHDashboardController

EmployeeDashboardController

EventManagementController

EmployeeEventsController

NotificationsController

DAO (Data Access Object)
Couche d’accès aux données MySQL :

UserDAO

EventDAO

RegistrationDAO

NotificationDAO

Chaque DAO est responsable des requêtes SQL liées à une table spécifique.

Models
Représentation des entités métier :

User

Event

Registration

Notification

Enums (Role, Status)

Services
NotificationService centralise la logique métier liée aux notifications et rappels.

Session
Stocke l’utilisateur actuellement connecté et permet l’accès global sécurisé à ses informations.

5. Base de données

Base : sport_entreprise

Tables principales :

users

id

full_name

email

password_hash

role

status

created_at

events

id

sport

event_date

location

capacity

status

objective_id

created_by_rh

registrations

id

event_id

employee_id

status

registered_at

notifications

id

user_id

type

message

is_read

created_at

6. Logique métier automatisée

Fermeture automatique d’un événement lorsque la capacité est atteinte

Blocage de suppression si des inscriptions existent

Génération automatique de rappels avant événement

Mise à jour automatique du statut d’un objectif lié

7. Sécurité

Hashage des mots de passe avec BCrypt

Validation des champs côté application

Gestion des rôles

Suppression logique plutôt que suppression physique