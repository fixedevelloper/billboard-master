# Cahier des charges — Billboard Starter

Plateforme de mise en relation et de gestion locative d'espaces publicitaires (panneaux d'affichage)

*Document généré le 11/08/2026 à partir de l'analyse du code source existant (backend Java/Spring Boot + frontend Next.js). Il décrit le périmètre fonctionnel tel qu'implémenté et signale les points restant à clarifier.*

---

## 1. Présentation générale

### 1.1 Contexte

Billboard Starter est une plateforme web qui met en relation des **propriétaires de panneaux publicitaires** (billboards) et des **annonceurs** souhaitant y diffuser des campagnes publicitaires, avec la possibilité pour un **acheteur d'espace média** (media buyer) d'intervenir pour le compte d'un annonceur, sous la supervision d'une équipe d'**administration**.

### 1.2 Objectif du document

Formaliser le périmètre fonctionnel de la plateforme afin de :
- servir de référence commune entre équipe technique et parties prenantes métier ;
- documenter l'existant suite à des pertes de contexte constatées dans l'historique du projet (derniers commits intitulés sobrement « perte ») ;
- identifier les zones incomplètes ou à trancher avant mise en production.

### 1.3 Objectifs du projet

- Permettre à un propriétaire de publier ses panneaux et de suivre leur occupation.
- Permettre à un annonceur de rechercher, réserver, contractualiser, payer et diffuser une campagne sur un panneau.
- Sécuriser la transaction par un contrat signé électroniquement avant tout paiement, et un paiement confirmé avant toute diffusion de campagne.
- Offrir une supervision complète (comptes, transactions, contenus, avis) à une équipe d'administration.
- Informer les utilisateurs en temps réel de l'avancement de leurs démarches (notifications).

---

## 2. Acteurs et glossaire

| Acteur | Description |
|---|---|
| **Utilisateur (User)** | Compte de connexion de base (email/mot de passe). Un même utilisateur peut cumuler plusieurs profils métier. |
| **Annonceur (Advertiser)** | Entreprise qui réserve des panneaux et diffuse des campagnes publicitaires. |
| **Propriétaire (Billboard Owner)** | Entreprise ou particulier propriétaire d'un ou plusieurs panneaux, qui les publie et en gère la location. |
| **Media Buyer** | Acheteur d'espace pour le compte d'annonceurs tiers ; peut se voir déléguer le paiement d'une réservation, avec une limite de crédit. |
| **Administrateur** | Supervise l'ensemble de la plateforme (comptes, paiements, contenus, litiges). Un compte admin est initialisé au démarrage du système. |
| **Panneau (Billboard)** | Support publicitaire physique (statique, digital, trivision, écran LED). |
| **Réservation (Booking)** | Demande de location d'un panneau sur une période donnée. |
| **Contrat (Contract)** | Document contractuel liant propriétaire et annonceur pour une réservation, signé électroniquement par les deux parties. |
| **Campagne (Campaign)** | Contenu publicitaire soumis par l'annonceur pour diffusion sur le panneau réservé et payé. |
| **Épreuve créative (Creative Proof)** | Visuel soumis par l'annonceur et validé par le propriétaire avant production/pose. |
| **Tâche d'installation (Installation Task)** | Intervention de pose du visuel sur le panneau physique. |

---

## 3. Périmètre fonctionnel

### 3.1 Comptes et authentification

- Inscription avec choix d'un type de profil (annonceur, propriétaire ou media buyer) créant en un seul parcours le compte utilisateur puis le profil métier associé.
- Connexion / déconnexion par email et mot de passe ; jeton de session transmis via cookie sécurisé (non accessible en JavaScript).
- Consultation et modification du profil utilisateur, changement de mot de passe.
- Page « Espaces » permettant à un utilisateur multi-profils de naviguer entre ses espaces (annonceur / propriétaire / media buyer / admin) et d'ajouter un nouveau profil métier (« devenir annonceur », « devenir propriétaire »).
- Statut de compte (en attente de vérification / actif / désactivé / suspendu).

> ⚠️ **Point à trancher (§8.1)** : le mécanisme de vérification d'email n'est pas fonctionnel en l'état — aucun parcours ne permet actuellement à l'utilisateur de confirmer son adresse, et ce statut n'a aujourd'hui aucun effet bloquant à la connexion.

### 3.2 Espace Annonceur

- Recherche et consultation des panneaux disponibles (localisation, type, tarif).
- Création d'une réservation sur un panneau pour une période donnée.
- Suivi de ses réservations (liste, détail, statut).
- Consultation et signature électronique du contrat associé à une réservation.
- Paiement de la réservation (paiement direct ou délégation à un media buyer).
- Création, soumission et suivi de campagnes publicitaires liées à une réservation payée.
- Suivi de l'installation et consultation d'un rapport de performance par campagne.
- Consultation de son portefeuille (wallet) et historique de transactions.

### 3.3 Espace Propriétaire

- Publication et gestion de ses panneaux (fiche, photos multiples, tarif journalier, statut : disponible / réservé / en maintenance / inactif).
- Consultation des réservations reçues sur ses panneaux.
- Création et signature du contrat de location.
- Validation ou refus des épreuves créatives soumises par l'annonceur (avec retour/feedback).
- Approbation ou rejet des campagnes soumises (avec motif de rejet).
- Planification des tâches d'installation sur ses panneaux.
- Tableau de bord propriétaire (occupation, revenus).

### 3.4 Espace Media Buyer

- Création du profil media buyer (limite de crédit, montant déjà engagé).
- Prise en charge du paiement d'une réservation pour le compte d'un annonceur, sur invitation/délégation.
- Suivi des paiements effectués pour compte de tiers.

> ⚠️ **Point à trancher** : aucun parcours d'inscription dédié (« devenir media buyer ») n'existe côté interface, alors que l'API le permet déjà — à ajouter ou à confirmer comme volontairement absent (profil réservé, créé par l'admin par exemple).

### 3.5 Espace Administration

- Gestion des comptes annonceurs, propriétaires et media buyers (vérification, activation, ajustement de la limite de crédit, du taux de reversion propriétaire).
- Création d'autres comptes administrateurs et journal d'audit des actions effectuées.
- Supervision de toutes les réservations, paiements et campagnes de la plateforme.
- Modération des avis (approbation / rejet avec motif).
- Gestion du référentiel de villes.
- Gestion de la configuration générale de la plateforme (paramètres clé-valeur).

> ⚠️ **Point à trancher** : quatre niveaux de droits admin sont prévus dans le modèle de données (super-admin, modérateur, support, finance) mais ne sont pas encore différenciés — tout compte admin dispose aujourd'hui d'un accès total.

### 3.6 Avis (Reviews)

- Dépôt d'un avis noté (note + commentaire) sur un panneau ou un propriétaire.
- Modération obligatoire par l'administration avant publication (motif de modération).
- Calcul de la note moyenne par cible.

### 3.7 Reporting / performance

- Génération de rapports de performance par campagne, par occupation de panneau, par revenu propriétaire, ou analytique globale (impressions, interactions, revenus, taux d'occupation).

### 3.8 Portefeuille (Wallet)

- Portefeuille par utilisateur avec solde et devise.
- Dépôt et retrait, historique des transactions.

### 3.9 Gestion documentaire / stockage

- Téléversement de fichiers (photos de panneaux, visuels de campagne, preuves d'installation, etc.) sur un stockage compatible S3.

### 3.10 Notifications temps réel

- Notification en direct (fil de notifications, cloche avec compteur non lu) et par email selon l'événement.
- Marquage comme lu, purge automatique des notifications lues après un délai de rétention configurable.
- Événements couverts : création/confirmation/annulation/expiration de réservation, réception ou échec de paiement, demande et confirmation de délégation de paiement, contrat prêt à signer / à mon tour de signer / entièrement signé, campagne approuvée / rejetée.

### 3.11 Internationalisation

- Interface disponible en français et en anglais, avec le français comme langue principale.

---

## 4. Parcours principal : de la réservation à la campagne

```
1. RÉSERVATION
   L'annonceur sélectionne un panneau disponible et une période.
   → Vérification automatique de non-chevauchement avec une réservation existante.
   → Statut : PENDING → CONFIRMED (par le propriétaire ou l'admin).
   → Expiration automatique si aucun paiement abouti au-delà d'un délai
     configurable (7 jours par défaut).

2. CONTRAT
   Un contrat est établi entre propriétaire et annonceur (conditions générales).
   → Publication pour signature → signature du propriétaire → signature de
     l'annonceur (nom + IP horodatés) → contrat SIGNÉ.
   → Tant que le contrat n'est pas signé par les deux parties, aucun paiement
     ne peut être initié.

3. PAIEMENT
   Paiement en ligne (Flutterwave) ou marquage manuel, par l'annonceur ou par
   un media buyer délégué.
   → Vérification indépendante du statut auprès de la passerelle de paiement
     (jamais confiance dans le seul retour navigateur).
   → Contrôle du montant et de la devise avant validation finale.

4. CAMPAGNE
   Une fois le paiement confirmé, l'annonceur peut créer et soumettre sa
   campagne (visuel, description).
   → Le propriétaire (ou l'admin) approuve ou rejette la campagne.
   → En parallèle : soumission d'une épreuve créative validée par le
     propriétaire, puis planification et réalisation de la tâche
     d'installation sur site (avec photo de preuve).
```

> ⚠️ **Point à trancher** : les statuts « active » et « terminée » d'une campagne existent dans le modèle mais aucune règle métier n'est aujourd'hui codée pour les déclencher automatiquement (fin d'installation ? action manuelle ?) — à spécifier.

---

## 5. Exigences non fonctionnelles

| Exigence | État constaté |
|---|---|
| **Sécurité des sessions** | Jeton d'authentification transmis en cookie HttpOnly sécurisé, jamais exposé côté JavaScript ni dans les réponses API. |
| **Contrôle d'accès** | Vérifié à chaque appel API selon le ou les profils du compte connecté (propriétaire d'une ressource, ou administrateur). |
| **Confidentialité mots de passe** | Chiffrement par hachage (bcrypt) et politique de robustesse appliquée à l'inscription. |
| **Traçabilité** | Journal d'audit des actions administrateur. |
| **Disponibilité multi-instance** | ⚠️ Les notifications temps réel reposent sur un registre en mémoire propre à chaque instance serveur ; un déploiement à plusieurs instances nécessiterait un bus de messages partagé (à prévoir si scaling horizontal envisagé). |
| **Multilinguisme** | Français / anglais, couverture complète des textes d'interface. |
| **Auditabilité paiement** | Double vérification (retour navigateur + notification serveur à serveur) et contrôle montant/devise avant validation d'un paiement. |
| **Tests automatisés** | ⚠️ Couverture très limitée à ce jour (un seul test, portant sur le respect des frontières entre modules) — à renforcer avant mise en production. |

---

## 6. Architecture technique (existant)

| Composant | Choix technique |
|---|---|
| Backend | Java 21, Spring Boot, organisé en modules métier indépendants (approche modulaire type DDD) |
| Frontend | Next.js / React, internationalisation intégrée |
| Base de données | MySQL, schéma géré par migrations versionnées |
| Stockage de fichiers | Service compatible S3 (photos, visuels, preuves) |
| Paiement | Flutterwave (paiement hébergé + vérification serveur + webhook) |
| Notifications temps réel | Flux d'événements serveur vers navigateur (SSE) |
| Emailing | Envoi SMTP |
| Déploiement | Conteneurisation Docker, intégration continue |

---

## 7. Périmètre fonctionnel par domaine (synthèse)

| Domaine | Fonctions couvertes |
|---|---|
| Comptes / Auth | Inscription multi-profils, connexion, gestion de profil, mot de passe |
| Panneaux | Publication, recherche, fiche détaillée, photos, statut |
| Réservations | Création, confirmation, annulation, expiration automatique |
| Contrats | Rédaction, publication, double signature électronique |
| Paiements | Initiation, paiement en ligne Flutterwave, délégation media buyer, vérification |
| Campagnes | Création, soumission, approbation/rejet |
| Épreuves créatives | Soumission, validation/révision |
| Installations | Planification, démarrage, clôture avec preuve photo |
| Avis | Dépôt, modération, note moyenne |
| Reporting | Génération de rapports de performance |
| Notifications | Temps réel + email, historique, compteur non lu |
| Portefeuille | Dépôt, retrait, historique |
| Administration | Supervision globale, audit, configuration plateforme, référentiel villes |

---

## 8. Limitations connues et points à trancher avant mise en production

1. **Vérification d'email non opérationnelle** — aucun parcours utilisateur ne permet de confirmer son adresse ; le statut du compte n'est par ailleurs pas contrôlé à la connexion. *Décision attendue : réactiver un vrai parcours de vérification, ou supprimer cette notion si elle n'est pas requise pour le lancement.*
2. **Paiement Flutterwave non configuré par défaut** — les clés d'API de production doivent être fournies avant mise en ligne.
3. **Notifications temps réel limitées à une seule instance serveur** — à revoir si un déploiement à plusieurs instances est prévu.
4. **Niveaux de droits administrateur non différenciés** — le modèle prévoit 4 rôles (super-admin, modérateur, support, finance) mais tout accès admin est aujourd'hui total.
5. **Absence de profil « technicien » dédié** — la tâche d'installation référence un identifiant libre, sans gestion d'équipe de pose ni interface associée côté administration.
6. **Transitions finales de campagne (active / terminée) non automatisées** — règle métier à spécifier.
7. **Couverture de tests automatisés très faible** — à renforcer, notamment sur les parcours critiques (paiement, signature de contrat).
8. **Parcours « devenir media buyer » absent du frontend** alors que l'API existe déjà — à confirmer comme choix voulu ou à compléter.
9. **Historique récent du dépôt de code** — les trois derniers commits (intitulés « perte ») suggèrent un incident suivi d'une restauration ; il est recommandé de vérifier avec l'équipe que rien n'a été perdu par rapport à la version de référence.

---

## 9. Annexe — Statuts métier (enums)

| Entité | Valeurs de statut |
|---|---|
| Utilisateur | en attente de vérification, actif, désactivé, suspendu |
| Panneau | disponible, réservé, en maintenance, inactif |
| Réservation | en attente, confirmée, annulée, refusée, terminée, expirée |
| Contrat | brouillon, en attente de signature, signé, résilié, expiré |
| Paiement | en attente, réussi, échoué, remboursé |
| Campagne | brouillon, soumise, approuvée, rejetée, active, terminée |
| Épreuve créative | (selon version) en attente, approuvée, révision demandée |
| Avis | (selon modération) en attente, approuvé, rejeté |

---

*Ce document reflète l'état du code source à la date indiquée. Il devra être validé et complété avec l'équipe métier, en particulier sur les 9 points de la section 8, avant d'être utilisé comme référence contractuelle.*
