# 🎓 COMPRENDRE TON PROJET DE A À Z — Guide de Soutenance

> **Hamza, lis ce document en entier ce soir. Demain tu seras prêt.**
> Tout est expliqué en langage simple, comme si je te parlais en face.

---

# PARTIE 1 : C'EST QUOI TON PROJET ?

## En une phrase

Tu as construit un **site web sécurisé** qui permet à l'ONEE (l'office de l'eau et de l'électricité du Maroc) de gérer ses fournisseurs en ligne, au lieu de le faire avec des papiers et des emails.

## Le problème que tu résous

Imagine l'ONEE. C'est un énorme organisme public. Ils achètent des tonnes de trucs : des câbles électriques, du ciment, des pompes à eau, des services de maintenance... Ils travaillent avec des centaines de fournisseurs.

**Avant ton projet, comment ça se passait ?**

- Un fournisseur voulait travailler avec l'ONEE ? Il devait se déplacer physiquement pour déposer un dossier papier.
- Les documents (registre de commerce, attestation fiscale...) étaient vérifiés manuellement, un par un.
- Les appels d'offres étaient affichés sur un tableau ou envoyés par email.
- Les factures étaient suivies dans des fichiers Excel.
- Aucune traçabilité : si quelqu'un modifiait un dossier, personne ne savait qui, quand, ni pourquoi.
- Zéro sécurité : les emails avec les documents sensibles passaient en clair.

**Le gros problème de fond :** La DGSSI (c'est la Direction Générale de la Sécurité des Systèmes d'Information au Maroc — l'équivalent de l'ANSSI en France) a dit : « Stop. Tous les organismes publics doivent sécuriser leurs applications. » C'est la loi. L'ONEE n'avait pas de plateforme conforme.

**Ta solution :** Un portail web où tout se fait en ligne, de manière sécurisée et traçable.

---

# PARTIE 2 : QUI UTILISE TON APPLICATION ?

Il y a **3 types d'utilisateurs** (on les appelle des "rôles") :

## 👨‍💼 1. L'Administrateur (ADMIN)

C'est le chef. Il voit tout, il contrôle tout.

**Ce qu'il peut faire :**
- Voir tous les fournisseurs inscrits
- Valider ou refuser un nouveau fournisseur qui s'est inscrit
- Gérer les comptes utilisateurs (créer, bloquer, supprimer)
- Consulter le journal d'audit (= l'historique de TOUT ce qui s'est passé sur la plateforme)
- Voir le tableau de bord avec les chiffres clés

**Imagine-le comme :** Le directeur du système. Il a les clés de tout.

## 🛒 2. Le Service Achat (SERVICE_ACHAT)

C'est le département qui gère les achats et les marchés publics.

**Ce qu'il peut faire :**
- Créer des appels d'offres (genre : "On cherche un fournisseur de câbles électriques, budget 500 000 DH, date limite le 15 juillet")
- Publier ces appels d'offres pour que les fournisseurs les voient
- Regarder les candidatures reçues et décider : on accepte ou on refuse
- Valider ou refuser les factures que les fournisseurs envoient
- Valider ou refuser les dossiers des fournisseurs

**Imagine-le comme :** L'acheteur. C'est lui qui lance les marchés et choisit les fournisseurs.

## 🏭 3. Le Fournisseur (SUPPLIER)

C'est l'entreprise externe qui veut vendre ses produits ou services à l'ONEE.

**Ce qu'il peut faire :**
- S'inscrire sur le portail avec son numéro ICE (c'est obligatoire, on en reparle)
- Envoyer ses documents administratifs (registre de commerce, attestation CNSS, etc.)
- Voir les appels d'offres publiés
- Répondre à un appel d'offres (= envoyer sa candidature avec un prix proposé)
- Envoyer des factures quand sa candidature est acceptée
- Suivre l'état de ses demandes

**Imagine-le comme :** Le commerçant qui frappe à la porte de l'ONEE pour proposer ses services.

**IMPORTANT pour la soutenance :** Le fournisseur ne voit QUE ses propres données. Il ne peut jamais voir les infos d'un autre fournisseur. C'est le **principe du moindre privilège** — chacun n'a accès qu'à ce dont il a besoin.

---

# PARTIE 3 : COMMENT ÇA MARCHE ? (Les modules)

Ton application a **6 modules**. Pense à chaque module comme une pièce dans une maison.

## 🔐 Module 1 : Authentification (la porte d'entrée)

C'est comment les gens se connectent et créent leur compte.

### Inscription d'un fournisseur

1. Le fournisseur remplit un formulaire : nom de l'entreprise, numéro ICE, catégorie, email...
2. Son compte est créé mais **il ne peut PAS se connecter tout de suite**. Son compte est en statut "en attente de validation".
3. L'administrateur regarde la demande et décide : je valide ou je refuse.
4. Si c'est validé → le fournisseur reçoit un **email avec un mot de passe temporaire**.
5. À sa première connexion, il est **obligé de changer ce mot de passe**.

**Pourquoi c'est comme ça ?** Pour empêcher n'importe qui de créer un faux compte fournisseur. C'est une exigence de sécurité DGSSI.

### Connexion (Login)

1. L'utilisateur tape son nom d'utilisateur + mot de passe.
2. Le serveur vérifie si c'est correct.
3. Si oui → le serveur génère un **jeton JWT** (on en reparle en détail) et le renvoie au navigateur.
4. Le navigateur stocke ce jeton et l'envoie à chaque requête suivante pour prouver son identité.

### Mot de passe oublié

1. L'utilisateur clique sur "Mot de passe oublié ?" et tape son email.
2. Le serveur envoie un email avec un lien sécurisé.
3. Ce lien contient un **jeton à usage unique** qui expire au bout de **30 minutes**.
4. L'utilisateur clique sur le lien, tape un nouveau mot de passe, c'est fait.

**Point de sécurité important :** Quand quelqu'un tape un email pour réinitialiser son mot de passe, le serveur répond TOUJOURS "Si cette adresse est associée à un compte, un lien vous a été envoyé." Même si l'email n'existe pas ! **Pourquoi ?** Pour empêcher un attaquant de deviner quels emails sont enregistrés. Ça s'appelle le **principe de non-énumération**.

### Verrouillage du compte

Si quelqu'un se trompe de mot de passe **5 fois de suite**, le compte est automatiquement verrouillé. C'est pour bloquer les attaques par force brute (quand un hacker essaye plein de mots de passe au hasard).

## 📋 Module 2 : Gestion des fournisseurs

C'est le cœur du système. Un fournisseur passe par plusieurs **états** (comme les étapes d'un processus) :

```
DRAFT (Brouillon)
    ↓ le fournisseur soumet sa fiche
PENDING_VALIDATION (En attente de validation)
    ↓ l'admin/service achat décide
VALIDATED (Validé) ←→ SUSPENDED (Suspendu)
    ou
REJECTED (Rejeté)
```

- **DRAFT** : le fournisseur vient de s'inscrire, il peut encore modifier sa fiche.
- **PENDING_VALIDATION** : il a soumis sa fiche, maintenant c'est entre les mains de l'admin.
- **VALIDATED** : tout est bon, le fournisseur est actif.
- **REJECTED** : le dossier est refusé.
- **SUSPENDED** : le fournisseur était validé mais on l'a temporairement suspendu (par exemple pour un problème de conformité). Il peut être réintégré.

**C'est quoi l'ICE ?** L'Identifiant Commun de l'Entreprise. C'est un numéro à **15 chiffres** attribué par l'administration fiscale marocaine à toute entreprise. Comme le numéro SIRET en France. Dans ton application, ce numéro est **obligatoire** et **validé automatiquement** (le système vérifie que c'est bien 15 chiffres, pas plus, pas moins).

**Les catégories de fournisseurs :** Il y en a 21, réparties en 4 groupes :
- Travaux (génie civil, canalisations, équipements hydrauliques...)
- Fournitures (matériel électrique, compteurs, produits chimiques...)
- Services (études techniques, maintenance, formation...)
- Général (divers)

Ce sont les catégories spécifiques de l'ONEE Branche Eau.

## 📄 Module 3 : Documents administratifs

Chaque fournisseur doit déposer des documents pour prouver qu'il est en règle. Il y a **7 types de documents** :

1. **RC_EXTRACT** : Extrait du Registre de Commerce
2. **TAX_COMPLIANCE_CERTIFICATE** : Attestation de conformité fiscale
3. **CNSS_ATTESTATION** : Attestation CNSS (sécurité sociale)
4. **ICE_CERTIFICATE** : Certificat ICE
5. **ISO_CERTIFICATION** : Certification ISO (qualité)
6. **BANK_RIB** : Relevé d'Identité Bancaire
7. **OTHER** : Autre document

Chaque document a un **cycle de validation** :

```
PENDING_REVIEW (En attente de vérification)
    ↓
APPROVED (Approuvé) ou REJECTED (Rejeté)
    ↓
EXPIRED (Expiré) — quand la date d'expiration est dépassée
```

Le système enregistre **qui** a validé le document, **quand**, et avec quel **commentaire**.

## 📢 Module 4 : Appels d'offres et candidatures

### Appels d'offres

Le service achat crée un appel d'offres avec :
- Un titre (ex : "Fourniture de câbles électriques haute tension")
- Une description détaillée
- Une catégorie
- Un budget estimé
- Une date de clôture

L'appel d'offres passe par ces états :

```
BROUILLON (pas encore visible)
    ↓ publication
PUBLIE (visible par les fournisseurs)
    ↓ date de clôture atteinte
CLOS (plus possible de candidater)

À tout moment avant la clôture :
ANNULE (l'appel d'offres est annulé)
```

### Candidatures (Demandes)

Quand un fournisseur voit un appel d'offres publié qui l'intéresse :
1. Il soumet une **candidature** avec une proposition technique et un montant proposé.
2. **Règle importante :** Un fournisseur ne peut envoyer qu'UNE SEULE candidature par appel d'offres. C'est bloqué en base de données.
3. Le service achat examine la candidature et décide : **ACCEPTEE** ou **REJETEE** (avec un commentaire).

## 💰 Module 5 : Facturation

Quand la candidature d'un fournisseur est acceptée (= il a gagné le marché), il peut émettre des factures.

Une facture contient :
- Une référence
- Un montant
- Une date d'émission
- Une date d'échéance (= quand il veut être payé)

Cycle de vie de la facture :

```
EN_ATTENTE (soumise, pas encore traitée)
    ↓
VALIDEE (vérifiée et acceptée)
    ↓
PAYEE (le fournisseur a été payé)

ou

EN_ATTENTE → REJETEE (la facture est refusée)
```

## 📝 Module 6 : Audit et traçabilité

C'est le module "Big Brother". **TOUT** ce qui se passe sur la plateforme est enregistré dans un journal.

**24 types d'actions** sont tracés, par exemple :
- LOGIN_SUCCESS / LOGIN_FAILURE (connexion réussie ou échouée)
- PASSWORD_RESET_REQUESTED (demande de reset mot de passe)
- SUPPLIER_REGISTERED / SUPPLIER_VALIDATED / SUPPLIER_REJECTED
- DOCUMENT_UPLOADED / DOCUMENT_APPROVED
- Et plein d'autres...

Pour chaque action, on enregistre :
- **Qui** l'a faite (quel utilisateur)
- **Quand** (horodatage précis)
- **Quoi** (quel type d'action)
- **Sur quoi** (quel fournisseur, quel document...)
- **L'adresse IP** de l'ordinateur
- **Le résultat** (succès ou échec)
- **Un commentaire** détaillé

**Ce journal est IMMUABLE** = on ne peut que LIRE et AJOUTER, jamais modifier ni supprimer. C'est une exigence DGSSI. En cas d'incident de sécurité, on peut retracer exactement ce qui s'est passé.

---

# PARTIE 4 : L'ARCHITECTURE (Comment c'est construit)

## L'architecture globale en 3 couches

Imagine ton application comme un sandwich à 3 étages :

```
┌─────────────────────────────────────────┐
│         FRONT-END (le navigateur)       │  ← Ce que l'utilisateur VOIT
│    HTML + CSS + JavaScript natif        │
└───────────────────┬─────────────────────┘
                    │ API REST (JSON)
                    ↓
┌─────────────────────────────────────────┐
│           BACK-END (le serveur)         │  ← La logique métier
│          Spring Boot 3 + Java 21        │
└───────────────────┬─────────────────────┘
                    │ SQL (requêtes)
                    ↓
┌─────────────────────────────────────────┐
│       BASE DE DONNÉES (MySQL 8.0)       │  ← Le stockage des données
└─────────────────────────────────────────┘
```

- Le **front-end** (le navigateur) envoie des requêtes HTTP au serveur.
- Le **back-end** (Spring Boot) traite ces requêtes, vérifie les droits, applique les règles métier.
- La **base de données** (MySQL) stocke toutes les données de manière permanente.

## La Clean Architecture (architecture hexagonale)

C'est LE concept clé de ton projet. Si le jury te pose une seule question technique, ce sera probablement celle-là.

### C'est quoi en termes simples ?

Imagine que ton application est un oignon avec 3 couches :

```
┌─────────────────────────────────────────────────────┐
│ INFRASTRUCTURE (la couche extérieure)               │
│  → Base de données, emails, sécurité, contrôleurs   │
│                                                      │
│   ┌─────────────────────────────────────────────┐    │
│   │ APPLICATION (la couche du milieu)            │    │
│   │  → Services qui orchestrent la logique       │    │
│   │                                              │    │
│   │   ┌─────────────────────────────────────┐    │    │
│   │   │ DOMAINE (le cœur)                   │    │    │
│   │   │  → Les règles métier pures          │    │    │
│   │   │  → User, Supplier, AppelOffre...    │    │    │
│   │   │  → ZÉRO dépendance externe          │    │    │
│   │   └─────────────────────────────────────┘    │    │
│   └─────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘
```

**La règle d'or :** Les flèches de dépendance pointent TOUJOURS vers l'intérieur. Le domaine ne connaît ni Spring, ni MySQL, ni JPA. Il ne connaît que ses propres règles.

### Pourquoi c'est bien ?

- **Testable** : tu peux tester le domaine sans base de données, sans serveur web, sans rien.
- **Maintenable** : si tu changes de base de données (MySQL → PostgreSQL), tu ne touches que la couche infrastructure. Le domaine ne bouge pas.
- **Clair** : chaque couche a une responsabilité précise. Pas de mélange.

### Comment c'est organisé dans ton code ?

```
com/supplierportal/
├── domain/                    ← LE CŒUR (zéro Spring, zéro JPA)
│   ├── user/
│   │   ├── User.java          ← L'entité utilisateur
│   │   ├── Role.java          ← ADMIN, SUPPLIER, SERVICE_ACHAT
│   │   └── UserRepository.java ← Interface (contrat), PAS l'implémentation
│   ├── supplier/
│   │   ├── Supplier.java
│   │   ├── SupplierStatus.java
│   │   └── SupplierCategory.java
│   ├── appeloffre/
│   ├── demande/
│   ├── facture/
│   ├── document/
│   ├── audit/
│   └── shared/
│       ├── valueobject/       ← IceNumber, Email (objets de valeur)
│       └── exception/        ← Exceptions métier
│
├── application/               ← LES SERVICES (orchestration)
│   ├── auth/
│   │   └── service/
│   │       ├── AuthenticationService.java
│   │       └── PasswordResetService.java
│   ├── supplier/
│   │   └── service/
│   │       └── SupplierService.java
│   ├── appeloffre/
│   ├── demande/
│   ├── document/
│   └── facture/
│
└── infrastructure/            ← LES DÉTAILS TECHNIQUES
    ├── persistence/           ← JPA, Hibernate, mappers
    ├── security/              ← JWT, BCrypt, Spring Security
    ├── email/                 ← Envoi d'emails (Jakarta Mail)
    └── web/
        ├── controller/        ← Les endpoints REST
        └── dto/               ← Les objets de requête/réponse
```

### Les concepts clés à retenir

**Port** = une interface (un contrat) définie dans le domaine. Exemple : `UserRepository` dit "il me faut une méthode pour chercher un utilisateur par nom d'utilisateur", mais il ne dit pas COMMENT.

**Adaptateur** = l'implémentation concrète d'un port, dans la couche infrastructure. Exemple : `UserRepositoryImpl` implémente `UserRepository` en utilisant JPA et MySQL.

**Objet de valeur (Value Object)** = un objet immuable qui représente un concept métier. Exemples :
- `IceNumber` : encapsule le numéro ICE. Vérifie automatiquement que c'est 15 chiffres. Une fois créé, on ne peut plus le modifier.
- `Email` : encapsule une adresse email. Vérifie le format automatiquement.

**Si le jury te demande : "Pourquoi pas une architecture classique ?"**
Réponse : "Parce que la Clean Architecture sépare le domaine métier des détails techniques. Si demain on change de base de données ou de framework, on ne touche que la couche infrastructure. Le code métier reste intact. C'est plus maintenable et plus testable."

---

# PARTIE 5 : LA SÉCURITÉ (le plus important pour ta soutenance)

C'est la partie DGSSI. Le jury va probablement te poser des questions là-dessus. Voici les 8 mécanismes de sécurité de ton application.

## 🔑 1. JWT (JSON Web Token) — L'authentification

**C'est quoi ?** Un "badge" numérique que le serveur donne à l'utilisateur quand il se connecte.

**Comment ça marche (en simple) :**

```
1. L'utilisateur envoie : "Je suis Hamza, mon mot de passe est 1234"
2. Le serveur vérifie → OK c'est correct
3. Le serveur crée un JETON (une longue chaîne de caractères) qui contient :
   - Le nom d'utilisateur : "hamza"
   - Son rôle : "SUPPLIER"
   - La date d'expiration : dans 1 heure
   - Une SIGNATURE (pour prouver que c'est le serveur qui l'a créé)
4. Le serveur renvoie ce jeton au navigateur
5. Le navigateur stocke le jeton dans sessionStorage
6. À chaque requête suivante, le navigateur envoie ce jeton
   dans l'en-tête HTTP : "Authorization: Bearer eyJhbGci..."
7. Le serveur vérifie la signature du jeton et sait qui est l'utilisateur
```

**Pourquoi pas des sessions classiques ?** Les sessions stockent l'état côté serveur (dans la mémoire du serveur). Avec JWT, le serveur est **stateless** = il ne stocke rien. Tout est dans le jeton. C'est plus simple à gérer, et si tu as plusieurs serveurs, ils peuvent tous vérifier le même jeton sans partager de mémoire.

**Le jeton est signé avec HMAC-SHA** et une clé secrète d'au moins 32 caractères.

## 🔒 2. BCrypt — Le hachage des mots de passe

**C'est quoi ?** Un algorithme qui transforme un mot de passe en une chaîne illisible. C'est **irréversible** : on ne peut pas retrouver le mot de passe à partir du haché.

**Exemple :**
```
Mot de passe : "MonMotDePasse123"
Haché BCrypt : "$2a$12$LJ3MFl9LoUjJHCjgGKUQYeEq3jHnGLrFPBW..."
```

**Pourquoi BCrypt et pas MD5 ou SHA-256 ?**
- BCrypt est **lent exprès**. Il faut du temps pour calculer un haché (facteur de coût 12 dans ton cas). Si un hacker vole ta base de données, il lui faudrait des millions d'années pour retrouver les mots de passe en testant toutes les combinaisons.
- BCrypt intègre un **sel aléatoire** (salt) : même si deux utilisateurs ont le même mot de passe, leurs hachés seront différents.
- MD5 et SHA-256 sont trop rapides → un hacker peut tester des milliards de combinaisons par seconde.

**Facteur de coût 12 :** C'est le niveau de difficulté. Plus c'est élevé, plus c'est lent (et sécurisé). 12 est le minimum recommandé par la DGSSI.

## 🛡️ 3. RBAC — Le contrôle d'accès par rôle

**C'est quoi ?** Chaque endpoint de l'API est protégé par une annotation `@PreAuthorize` qui vérifie le rôle de l'utilisateur CÔTÉ SERVEUR.

**Exemple concret dans le code :**
```java
@PreAuthorize("hasRole('SERVICE_ACHAT')")
public ResponseEntity<AppelOffre> create(...) {
    // Seul le service achat peut créer un appel d'offres
}
```

Si un fournisseur essaye d'appeler cet endpoint → il reçoit une erreur 403 Forbidden (accès interdit).

**Point crucial :** Le contrôle est fait côté SERVEUR, pas côté navigateur. Même si quelqu'un modifie le JavaScript dans son navigateur pour afficher un bouton "Créer un appel d'offres", le serveur refusera la requête. Le client n'est jamais digne de confiance.

## 🔄 4. Jetons de réinitialisation — Usage unique + SHA-256

**Quand l'utilisateur oublie son mot de passe :**

1. Le serveur génère un jeton aléatoire (genre `a7f3b2c8d9e1...`)
2. Il envoie ce jeton par email dans un lien
3. En base de données, il ne stocke PAS le jeton en clair. Il stocke son **empreinte SHA-256** (un haché)
4. Quand l'utilisateur clique sur le lien, le serveur re-calcule le SHA-256 du jeton reçu et compare avec ce qui est en base

**Pourquoi stocker le haché et pas le jeton en clair ?** Si un hacker vole la base de données, il aura les hachés mais ne pourra pas les utiliser pour réinitialiser les mots de passe. C'est le même principe que pour les mots de passe.

Le jeton expire après **30 minutes** et est **à usage unique** (une fois utilisé, il est marqué comme tel et ne peut plus servir).

## ⏱️ 5. Rate Limiting — Limitation de débit

**C'est quoi ?** Si quelqu'un essaye de se connecter trop de fois trop vite (= attaque par force brute), le système bloque les tentatives.

Le `AuthenticationRateLimiter` limite le nombre de tentatives par combinaison **adresse IP + nom d'utilisateur**. Après le seuil, on reçoit : "Trop de tentatives. Réessayez dans une minute."

## 🙈 6. Non-énumération des comptes

**C'est quoi ?** Le système ne révèle jamais si un compte existe ou non.

**Exemples :**
- Login échoué → "Identifiants incorrects" (pas "Cet utilisateur n'existe pas")
- Mot de passe oublié → "Si cette adresse est associée à un compte..." (toujours le même message)

**Pourquoi ?** Si un attaquant peut savoir quels noms d'utilisateur existent, il n'a plus qu'à trouver le mot de passe. En masquant cette info, on double la difficulté pour l'attaquant.

## 🔐 7. En-têtes de sécurité HTTP

Ce sont des instructions que le serveur envoie au navigateur pour limiter les risques :

| En-tête | Ce que ça fait | En simple |
|---------|----------------|-----------|
| **CSP** (Content-Security-Policy) | Limite les sources de scripts/styles autorisées | Empêche l'injection de code malveillant (XSS) |
| **HSTS** (Strict-Transport-Security) | Force HTTPS | Empêche les connexions non chiffrées |
| **X-Frame-Options: DENY** | Interdit l'intégration dans un iframe | Empêche le clickjacking |
| **X-Content-Type-Options: nosniff** | Empêche le MIME sniffing | Le navigateur respecte le type de fichier déclaré |
| **Referrer-Policy** | Limite les infos envoyées dans le Referer | Protège la vie privée |

## 📋 8. Journal d'audit inaltérable

Comme expliqué plus haut, TOUT est enregistré. C'est un log **append-only** : on ne peut que AJOUTER, jamais modifier ni supprimer. C'est une exigence clé de la DGSSI pour la traçabilité.

---

# PARTIE 6 : LA BASE DE DONNÉES

## Les 11 tables

Ton application utilise **MySQL 8.0** et a **11 tables** :

| Table | C'est quoi | Colonnes importantes |
|-------|------------|---------------------|
| `users` | Les comptes utilisateurs | username, email, password_hash, role, status, failed_login_attempts |
| `suppliers` | Les fiches fournisseurs | company_name, ice (15 chiffres), category, status |
| `supplier_documents` | Les pièces justificatives | document_type, file_reference, status, expiry_date |
| `supplier_evaluations` | Les évaluations de fournisseurs | overall_score, comment |
| `evaluation_criterion_scores` | Les notes par critère | criterion, score |
| `appel_offres` | Les appels d'offres | titre, budget_estime, date_cloture, status |
| `demandes` | Les candidatures | proposition_technique, montant_propose, status |
| `factures` | Les factures | reference_facture, montant, date_echeance, status |
| `audit_log` | Le journal d'audit | actor_user_id, action_type, ip_address, outcome |
| `password_reset_tokens` | Les jetons de reset | token_hash, expiry_date, used |
| `notifications` | Les notifications internes | message, is_read |

## Flyway — Les migrations

**C'est quoi Flyway ?** Un outil qui gère l'évolution de ta base de données de manière versionnée. Au lieu de modifier les tables à la main, tu écris des scripts SQL numérotés :

```
V1__initial_schema.sql        ← Les tables de base (users, suppliers, documents, audit...)
V2__password_reset_tokens.sql ← Ajout de la table des jetons de reset
V3__marketplace_schema.sql    ← Tables du marketplace (ensuite supprimées)
V4__procurement_schema.sql    ← Les tables des marchés (appels d'offres, demandes, factures)
V5__must_change_password.sql  ← Ajout du champ "doit changer son mot de passe"
V6__recreate_password_reset_tokens.sql ← Refonte de la table des jetons de reset
```

**Pourquoi c'est bien ?** On peut reproduire exactement la même base de données sur n'importe quel ordinateur. C'est traçable et versionné.

---

# PARTIE 7 : LES TECHNOLOGIES

## Pourquoi ces choix ?

| Technologie | Pourquoi ce choix |
|-------------|-------------------|
| **Java 21** | Langage robuste, typé statiquement (= les erreurs sont détectées à la compilation), très utilisé en entreprise et dans les organismes publics. Version LTS (= support à long terme). |
| **Spring Boot 3** | Le framework Java le plus utilisé au monde. Il fournit tout ce dont on a besoin : serveur web, injection de dépendances, sécurité, accès aux données. |
| **Spring Security 6** | Le module de sécurité de Spring. Il gère l'authentification, l'autorisation, le chiffrement. C'est le standard de l'industrie pour les applications Java. |
| **MySQL 8.0** | Base de données relationnelle fiable, gratuite, très répandue. Compatible avec les exigences de l'ONEE. |
| **JWT (jjwt)** | Standard ouvert (RFC 7519) pour l'authentification stateless. Recommandé par la DGSSI pour les API REST. |
| **HTML/CSS/JS natif** | Pas de framework front-end (React, Angular...). Choix pédagogique : maîtrise totale du code, pas de dépendance tierce côté client. |
| **Flyway** | Gestion propre et versionnée du schéma de base de données. |
| **Lombok** | Réduit le code boilerplate Java (getters, setters, constructeurs...). |
| **Jakarta Mail** | Envoi d'emails (SMTP) directement depuis Java, sans dépendance externe. |

## L'API REST — Comment le front-end parle au back-end

Le front-end (JavaScript dans le navigateur) envoie des requêtes HTTP au back-end :

```
Front-end                              Back-end
─────────                              ────────
   │                                      │
   │── POST /api/v1/auth/login ──────────>│  "Je veux me connecter"
   │<── 200 OK + jeton JWT ──────────────│  "Voilà ton badge"
   │                                      │
   │── GET /api/v1/suppliers ────────────>│  "Donne-moi la liste des fournisseurs"
   │   (avec le jeton dans l'en-tête)     │  (le serveur vérifie le jeton et le rôle)
   │<── 200 OK + liste JSON ────────────│  "Voilà les données"
   │                                      │
   │── POST /api/v1/appels-offres ──────>│  "Je veux créer un appel d'offres"
   │   (avec les données en JSON)         │  (le serveur vérifie que c'est SERVICE_ACHAT)
   │<── 201 Created ───────────────────│  "C'est créé"
```

Les données sont échangées en **JSON** (JavaScript Object Notation) — un format texte simple et lisible.

---

# PARTIE 8 : LE MODULE EMAIL

Ton application envoie des emails dans deux cas :

1. **Mot de passe temporaire** : quand l'admin valide un fournisseur, celui-ci reçoit un email avec son login et un mot de passe temporaire.
2. **Lien de réinitialisation** : quand quelqu'un demande un reset de mot de passe.

**Comment ça marche techniquement :**
- Le service `EmailService` utilise **Jakarta Mail** pour se connecter à un serveur SMTP (Gmail dans ton cas).
- L'envoi est **asynchrone** (`@Async`) : le serveur envoie l'email en arrière-plan et ne bloque pas la requête HTTP.
- Les emails sont en **HTML** avec un style orange professionnel (en-tête, bouton d'action, pied de page).
- Si le SMTP n'est pas configuré, le service **simule** l'envoi et affiche le contenu dans les logs (utile pour les tests).

---

# PARTIE 9 : QUESTIONS PROBABLES DU JURY

## Questions générales

**Q : Présentez votre projet en 2 minutes.**
R : "J'ai développé un portail fournisseur sécurisé pour l'ONEE. C'est une application web qui permet aux fournisseurs de s'inscrire en ligne, de déposer leurs documents administratifs, de répondre aux appels d'offres et de soumettre leurs factures. Le tout en conformité avec les normes de sécurité de la DGSSI. L'application utilise Spring Boot 3 avec une Clean Architecture, une API REST sécurisée par JWT, et une base de données MySQL."

**Q : Pourquoi ce projet ?**
R : "L'ONEE gérait ses fournisseurs manuellement : papiers, emails, Excel. Pas de traçabilité, pas de sécurité. La DGSSI impose aux organismes publics de sécuriser leurs applications. Ce portail résout ces deux problèmes."

**Q : C'est quoi la DGSSI ?**
R : "C'est la Direction Générale de la Sécurité des Systèmes d'Information, rattachée à l'Administration de la Défense Nationale du Maroc. Elle a publié la DNSSI, la Directive Nationale de Sécurité des Systèmes d'Information, qui impose des règles de cybersécurité aux organismes publics."

## Questions techniques

**Q : Pourquoi la Clean Architecture ?**
R : "Pour séparer le domaine métier des détails techniques. Mon domaine ne dépend ni de Spring ni de MySQL. Si je change de framework ou de base de données, seule la couche infrastructure est impactée. C'est plus maintenable et testable."

**Q : C'est quoi un JWT ?**
R : "C'est un jeton signé que le serveur génère après la connexion. Il contient le nom d'utilisateur, son rôle et la date d'expiration. Le client l'envoie à chaque requête pour prouver son identité. C'est stateless : le serveur ne stocke aucune session."

**Q : Pourquoi BCrypt et pas SHA-256 pour les mots de passe ?**
R : "BCrypt est conçu pour être lent, ce qui rend les attaques par force brute très coûteuses. SHA-256 est trop rapide : un attaquant peut tester des milliards de hash par seconde. BCrypt intègre aussi un sel aléatoire et un facteur de coût configurable."

**Q : Comment vous protégez contre les attaques par force brute ?**
R : "Trois mécanismes : le rate limiting qui bloque après trop de tentatives, le verrouillage du compte après 5 échecs, et BCrypt qui rend chaque vérification de mot de passe intentionnellement lente."

**Q : C'est quoi le principe de non-énumération ?**
R : "C'est le fait de ne jamais révéler si un compte existe ou non. Par exemple, quand on demande un reset de mot de passe, le serveur dit toujours le même message, que l'email existe ou pas. Ça empêche un attaquant de deviner la liste des comptes."

**Q : Comment vous assurez la traçabilité ?**
R : "Chaque action sensible est enregistrée dans un journal d'audit immuable. On stocke qui a fait quoi, quand, depuis quelle IP, sur quelle entité, avec quel résultat. C'est append-only : on ne peut que lire et ajouter, jamais modifier ou supprimer."

**Q : Pourquoi pas React ou Angular pour le front-end ?**
R : "C'est un choix pédagogique. La taille du projet ne justifiait pas un framework front-end. J'ai préféré le JavaScript natif pour garder la maîtrise totale du code et limiter les dépendances."

**Q : C'est quoi Flyway ?**
R : "C'est un outil de migration de base de données. Au lieu de modifier les tables à la main, j'écris des scripts SQL versionnés (V1, V2, V3...). Flyway les exécute dans l'ordre au démarrage de l'application. C'est reproductible et traçable."

**Q : Comment fonctionne l'inscription d'un fournisseur ?**
R : "Le fournisseur remplit un formulaire avec son ICE, sa raison sociale et ses coordonnées. Son compte est créé en statut 'en attente'. L'administrateur valide ou refuse. Si c'est validé, un mot de passe temporaire est généré et envoyé par email. À la première connexion, le fournisseur est obligé de le changer."

**Q : C'est quoi l'ICE ?**
R : "L'Identifiant Commun de l'Entreprise. C'est un numéro à 15 chiffres attribué par l'administration fiscale marocaine à toute entreprise. C'est l'équivalent du SIRET en France. Dans mon application, c'est un objet de valeur qui valide automatiquement le format."

**Q : Comment vous gérez les droits d'accès ?**
R : "Par le contrôle d'accès basé sur les rôles (RBAC). Chaque endpoint de l'API a une annotation @PreAuthorize qui vérifie le rôle de l'utilisateur côté serveur. Un fournisseur ne peut accéder qu'à ses propres données. Un admin a accès à tout. Le service achat gère les marchés."

**Q : Quelles sont les perspectives d'évolution ?**
R : "L'authentification multi-facteurs (2FA) pour renforcer la sécurité, une application mobile grâce au découplage API REST, la signature électronique pour les contrats, les notifications en temps réel via WebSocket, et un module de reporting avec des graphiques."

---

# PARTIE 10 : AIDE-MÉMOIRE RAPIDE POUR DEMAIN

## Les chiffres à retenir

| Quoi | Chiffre |
|------|---------|
| Rôles | **3** (Admin, Service Achat, Fournisseur) |
| Modules | **6** (Auth, Fournisseurs, Documents, Appels d'offres, Factures, Audit) |
| Tables en base | **11** |
| Types de documents | **7** |
| Catégories de fournisseurs | **21** (en 4 groupes) |
| Types d'actions d'audit | **24** |
| Migrations Flyway | **6** (V1 à V6) |
| ICE | **15 chiffres** |
| BCrypt facteur de coût | **12** |
| Échecs avant verrouillage | **5** |
| Durée du jeton de reset | **30 minutes** |
| Clé JWT minimum | **32 caractères** |

## Les mots-clés importants

- **Clean Architecture / Architecture hexagonale** : séparation domaine / application / infrastructure
- **API REST** : interface de communication entre front et back
- **JWT** : jeton d'authentification signé, stateless
- **BCrypt** : hachage de mot de passe avec sel et facteur de coût
- **RBAC** : contrôle d'accès basé sur les rôles
- **DGSSI / DNSSI** : direction de cybersécurité / directive nationale
- **Flyway** : migrations versionnées de base de données
- **Port & Adaptateur** : contrat (interface) et implémentation
- **Objet de valeur** : objet immuable qui encapsule un concept (IceNumber, Email)
- **Stateless** : le serveur ne stocke pas d'état de session
- **Append-only** : écriture seule, pas de modification ni suppression
- **Non-énumération** : ne pas révéler l'existence des comptes
- **Rate limiting** : limitation du nombre de requêtes
- **CSP, HSTS** : en-têtes de sécurité HTTP

## En cas de question que tu ne comprends pas

Dis : "C'est une bonne question. Dans le cadre de ce projet, j'ai privilégié [ce que tu connais] pour répondre aux exigences de la DGSSI et aux besoins de l'ONEE. C'est une piste d'amélioration que j'ai identifiée dans mes perspectives d'évolution."

---

**Bonne chance pour demain Hamza ! Tu vas gérer 💪🔥**
