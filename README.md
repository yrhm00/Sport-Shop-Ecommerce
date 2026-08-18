# JogginApp E-Shop — Site de vente en ligne sécurisé

Projet de l'unité **IG338 – Développement avancé d'application Web** (Henallux – IESN).
Application Spring Boot (MVC, architecture 3 couches) : catalogue dynamique, panier,
commande, paiement PayPal, internationalisation FR/EN et sécurisation complète.

> **Pressé ?** Va directement au [démarrage rapide](#2-démarrage-rapide-macos).

---

## 1. Prérequis

| Outil | Version | Vérifier |
|---|---|---|
| JDK | **17** (ni 21 ni 25) | `java -version` |
| Maven | 3.8+ | `mvn -v` |
| PostgreSQL | 14+ | `psql --version` |

### Installation sur macOS (Homebrew)

```bash
brew install openjdk@17 maven postgresql@16
```

Si tu as plusieurs JDK installés, ce n'est pas grave : le script de lancement
sélectionne automatiquement le 17. Pour le faire à la main :

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### Installation sur Windows

- JDK 17 : https://adoptium.net/temurin/releases/?version=17
- Maven : https://maven.apache.org/download.cgi
- PostgreSQL : https://www.postgresql.org/download/windows/

---

## 2. Démarrage rapide (macOS)

### Étape 1 — Démarrer PostgreSQL

**Installation officielle EnterpriseDB** (dossier `/Library/PostgreSQL/18`) :

```bash
sudo launchctl load /Library/LaunchDaemons/postgresql-18.plist
```

**Installation Homebrew** :

```bash
brew services start postgresql@16
```

Vérification (doit répondre `accepting connections`) :

```bash
pg_isready -h localhost -p 5432
```

> Si `pg_isready` n'est pas trouvé, préfixe les commandes PostgreSQL par leur
> chemin complet : `/Library/PostgreSQL/18/bin/pg_isready`, etc.

### Étape 2 — Créer la base de données

```bash
./init-db.sh
```

Le script vérifie que PostgreSQL répond, teste les identifiants, puis crée la base
`janvier_db`. **Si la base existe déjà, il demande confirmation avant de la
supprimer** (son contenu — comptes créés, commandes — serait définitivement perdu).

Pour ne pas être interrogé : `./init-db.sh --force`
Pour utiliser un autre nom de base : `DB_NAME=ma_base ./init-db.sh`

Équivalent manuel :

```bash
createdb -U postgres -h localhost janvier_db
```

Tu n'as **pas** à charger `schema.sql` toi-même : l'application crée les tables et
insère les données de test au démarrage.

> **Tu avais déjà une base `janvier_db` d'une version précédente du projet ?**
> Le schéma a changé (table unique de traduction, table des promotions, statut
> de commande). Il faut repartir d'une base vide : `./init-db.sh` s'en charge
> après confirmation.

### Étape 3 — Lancer l'application

```bash
./run.sh
```

Le script choisit le JDK 17, positionne les variables d'environnement et démarre
l'application. Équivalent manuel :

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17) && export DB_PASSWORD='Ilies2004_' && mvn spring-boot:run
```

### Étape 4 — Ouvrir le site

**https://localhost:8443/janvier**

Le navigateur affiche un avertissement de sécurité : c'est normal, le certificat
TLS est auto-signé (développement). Clique sur **Avancé → Continuer vers localhost**.

---

## 3. Configuration

Les paramètres se définissent par variables d'environnement. Les valeurs
ci-dessous sont celles du projet ; elles sont déjà dans `run.sh`.

| Variable | Valeur du projet | Rôle |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/janvier_db` | URL JDBC |
| `DB_USERNAME` | `postgres` | Utilisateur PostgreSQL |
| `DB_PASSWORD` | `Ilies2004_` | Mot de passe PostgreSQL de la machine de développement |
| `PAYPAL_CLIENT_ID` | voir ci-dessous | Application PayPal (sandbox) |
| `PAYPAL_CLIENT_SECRET` | voir ci-dessous | Application PayPal (sandbox) |
| `PAYPAL_MODE` | `sandbox` | `sandbox` ou `live` |
| `SSL_ENABLED` | `true` | Mettre `false` pour lancer en HTTP simple |
| `SERVER_PORT` | `8443` | Port d'écoute |
| `SSL_KEYSTORE_PASSWORD` | `ChangeMe2026` | Mot de passe du keystore TLS |

### Identifiants PayPal sandbox du projet

```
PAYPAL_CLIENT_ID=ASXWWhLhd_mx15AtVETBFrxq-4KsZicGJnon4ppqKqapddF8Ugkov7T4zRZNhtCWm5-szWaVu3Sj3ive
PAYPAL_CLIENT_SECRET=EKXVHcJ9TfPvJn08uuhEQZEkviCVHL7OXLK0YFRkUDNg1ozIXjL_yKlB6GWPXo99QzgMDvP_OBLcVjnr
```

Ce sont des identifiants **sandbox** : aucun argent réel ne circule. Pour finaliser
un paiement, il faut se connecter avec un **compte acheteur sandbox**, créé depuis
https://developer.paypal.com/dashboard/accounts (l'acheteur ne peut pas être le
même compte que le vendeur).

Si ces identifiants ont expiré, régénère-les dans le dashboard PayPal
(*Apps & Credentials → Sandbox*) et mets à jour `run.sh`.

### Lancer sans HTTPS (dépannage uniquement)

```bash
SSL_ENABLED=false SERVER_PORT=8082 ./run.sh
```

→ http://localhost:8082/janvier
Le TLS fait partie des exigences du projet : à utiliser seulement pour dépanner.

---

## 4. Comptes de démonstration

Créés automatiquement par `schema.sql`, mots de passe hachés en BCrypt.

| Login | Mot de passe | Rôles |
|---|---|---|
| `user1` | `MotDePasse2026!` | `ROLE_USER` |
| `user2` | `MotDePasse2026!` | `ROLE_USER` |
| `admin` | `AdminEshop2026!` | `ROLE_ADMIN`, `ROLE_USER` |

Tu peux évidemment aussi créer un compte via le formulaire d'inscription
(le mot de passe doit faire au moins 15 caractères).

---

## 5. Lancer les tests

```bash
mvn test
```

49 tests (JUnit + Mockito) : services (promotions, commandes, inscription, produits,
nettoyage XSS), panier, règles de promotion et retrait de stock.
Aucune base de données n'est nécessaire pour les tests.

---

## 6. Scénario de démonstration

1. **Visiteur non connecté** : parcourir le catalogue, ouvrir un produit, l'ajouter au panier, modifier la quantité, en supprimer un.
2. **Changer de langue** (FR/EN dans la bannière) : les libellés statiques *et* les noms/descriptions venant de la base sont traduits.
3. **Montrer que les promotions sont en base** — sans redémarrer l'application :
   ```bash
   psql -U postgres -h localhost -d janvier_db -c "UPDATE promotions SET valeur = 40 WHERE code='PROMO-MONTRES';"
   ```
   Recharger la page d'une montre : le prix a changé.
4. **S'inscrire**, se connecter : message personnalisé avec le login dans la bannière.
5. **Passer commande** : panier → récapitulatif → confirmation. Montrer en base que la commande existe **avant** le paiement :
   ```bash
   psql -U postgres -h localhost -d janvier_db -c "SELECT id, statut, paye, montant_total FROM orders;"
   ```
6. **Annuler sur PayPal** : la commande n'est pas supprimée, l'écran propose de payer plus tard ou d'annuler ; le panier est conservé.
7. **Payer** : la commande passe à `PAYEE` et le stock est décrémenté.
8. **Sécurité** : montrer le cadenas HTTPS, puis les en-têtes dans l'inspecteur du navigateur (`Content-Security-Policy`, `Strict-Transport-Security`, cookie `Secure; HttpOnly; SameSite=Lax`).

---

## 7. En cas de problème

| Message | Cause | Solution |
|---|---|---|
| `Schema-validation: missing table [...]` | La base contient l'ancien schéma | `dropdb` puis `createdb` (voir étape 2) |
| `password authentication failed for user "postgres"` | Mauvais `DB_PASSWORD` | Corriger la valeur dans `run.sh` |
| `Connection to localhost:5432 refused` | PostgreSQL n'est pas démarré | Étape 1 |
| `Port 8443 was already in use` | Une instance tourne déjà | `pkill -f spring-boot:run` |
| `class file has wrong version` / erreurs Hibernate au démarrage | Mauvais JDK (21 ou 25 au lieu de 17) | `export JAVA_HOME=$(/usr/libexec/java_home -v 17)` |
| Page sans mise en forme | Ressources statiques bloquées | Vérifier que l'URL commence bien par `/janvier` |
| Le bouton PayPal renvoie une erreur | Identifiants PayPal absents ou expirés | Voir section 3 |

Pour repartir totalement de zéro (base + compilation) :

```bash
./init-db.sh --force && mvn clean && ./run.sh
```

## Contenu du dépôt

| Fichier | Rôle |
|---|---|
| `init-db.sh` | Crée (ou recrée, après confirmation) la base de données |
| `run.sh` | Sélectionne le JDK 17, définit les variables d'environnement et démarre l'application |
| `src/main/resources/schema.sql` | Création des tables **et** données de test |
| `src/main/resources/keystore.p12` | Certificat TLS de développement |
| `pom.xml` | Dépendances et build Maven |

---

## 8. Certificat TLS

Le dépôt contient un certificat auto-signé de développement
(`src/main/resources/keystore.p12`, mot de passe `ChangeMe2026`).
Pour en régénérer un :

```bash
keytool -genkeypair -alias jogginapp -keyalg RSA -keysize 4096 -validity 3650 -dname "CN=localhost, OU=IESN, O=Henallux, L=Namur, C=BE" -keypass ChangeMe2026 -keystore src/main/resources/keystore.p12 -storetype PKCS12 -storepass ChangeMe2026 -ext "SAN=dns:localhost,ip:127.0.0.1"
```

---

## 9. Architecture

```
be.henallux.janvier
├── configuration     Sécurité, Tiles, i18n, PayPal
├── controller        Couche présentation (Spring MVC) + gestion centralisée des erreurs
├── service           Couche métier (promotions, commandes, inscription, nettoyage XSS)
├── model             Objets métier (aucune dépendance JPA)
├── dataAccess
│   ├── dao           Interfaces DataAccess + implémentations (abstraction de la persistance)
│   ├── repository    Spring Data JPA
│   ├── entity        Entités Hibernate
│   ├── projection    Résultats des requêtes produit/catégorie + traduction
│   └── util          Conversion entité <-> modèle (Dozer)
└── exception         Exceptions métier
```

Les contrôleurs ne manipulent jamais d'entités JPA ni de repositories : la couche
d'accès aux données expose des interfaces (`ProductDataAccess`, `OrderDataAccess`…)
et ne renvoie que des objets du paquet `model`.

## 10. Internationalisation

Trois niveaux, comme demandé au cahier des charges :

1. **Libellés statiques** : `messages_fr.properties` / `messages_en.properties` (245 clés synchronisées).
2. **Libellés dynamiques** (noms et descriptions venant de la base) : table **unique**
   `translations`, lue par **jointure** SQL filtrée sur la langue courante
   (voir `ProductRepository.SELECT_TRADUIT`).
3. **Messages d'erreur** : erreurs de validation et messages applicatifs traduits
   dans les mêmes fichiers de propriétés.

## 11. Promotions

Aucune promotion n'est codée en dur. Toutes les règles vivent dans la table
`promotions` et sont appliquées par `PromotionService` (couche métier) :

| Colonne | Rôle |
|---|---|
| `portee` | `PRODUIT`, `CATEGORIE` ou `PANIER` |
| `type_reduction` | `POURCENTAGE` ou `MONTANT` |
| `valeur` | Taux (%) ou montant fixe (€) |
| `seuil_min` | Montant minimum du panier (portée `PANIER`) |
| `product_id` / `category_id` | Cible de la promotion |
| `date_debut` / `date_fin` / `active` | Période de validité |

Changer une promotion = un `UPDATE` en base, sans recompilation.
Un produit ne reçoit jamais deux remises : seule la meilleure s'applique.

## 12. Sécurité

| Mesure | Mise en œuvre |
|---|---|
| Authentification | Spring Security, `UserDetailsService` sur la table `users` |
| Mots de passe | Hachage **BCrypt** |
| Injections SQL | Requêtes paramétrées (Spring Data JPA / JPQL), aucune concaténation |
| CSRF | Protection active, jeton dans tous les formulaires POST |
| Cookies | `SameSite=Lax`, `HttpOnly`, `Secure` |
| TLS | HTTPS + HSTS |
| CSP | `default-src 'self'` — aucun script ni style inline, aucun CDN |
| XSS | Nettoyage des saisies (OWASP HTML Sanitizer) + échappement `<c:out>` à l'affichage |
| Fixation de session | Nouvel identifiant de session à la connexion |
| Effets de bord | Aucune modification d'état en GET ; déconnexion en POST |
| Contrôle d'accès | Une commande n'est consultable et payable que par le client qui l'a passée |
