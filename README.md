# JogginApp E-Shop — Site de vente en ligne sécurisé

Projet de l'unité **IG338 – Développement avancé d'application Web** (Henallux – IESN).

Application Spring Boot (MVC + 3 couches) : catalogue dynamique, panier, commande,
paiement PayPal, internationalisation FR/EN et sécurisation complète.

---

## 1. Prérequis

| Outil | Version |
|---|---|
| JDK | 17 |
| Maven | 3.8+ |
| PostgreSQL | 14+ |

## 2. Création de la base de données

Le script `src/main/resources/schema.sql` crée **et** remplit la base. Il est exécuté
automatiquement au démarrage de l'application (`spring.sql.init.mode=always`).

```bash
createdb -U postgres janvier_db
```

> **Mise à jour depuis une version précédente du projet** : le schéma a changé
> (table unique de traduction, table des promotions, statut de commande). Il faut
> repartir d'une base vide :
>
> ```bash
> dropdb -U postgres janvier_db && createdb -U postgres janvier_db
> ```

## 3. Configuration

Aucun secret n'est stocké dans le dépôt. Les paramètres se définissent par variables
d'environnement ; les valeurs par défaut conviennent à une installation locale standard.

| Variable | Défaut | Rôle |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/janvier_db` | URL JDBC |
| `DB_USERNAME` | `postgres` | Utilisateur PostgreSQL |
| `DB_PASSWORD` | `postgres` | Mot de passe PostgreSQL |
| `PAYPAL_CLIENT_ID` | *(vide)* | Identifiant de l'application PayPal |
| `PAYPAL_CLIENT_SECRET` | *(vide)* | Secret de l'application PayPal |
| `PAYPAL_MODE` | `sandbox` | `sandbox` ou `live` |
| `SSL_KEYSTORE_PASSWORD` | `ChangeMe2026` | Mot de passe du keystore TLS |
| `SERVER_PORT` | `8443` | Port HTTPS |

```bash
export DB_PASSWORD='...'
export PAYPAL_CLIENT_ID='...'
export PAYPAL_CLIENT_SECRET='...'
```

## 4. Certificat TLS

Le dépôt contient un certificat auto-signé de développement
(`src/main/resources/keystore.p12`). Pour en régénérer un :

```bash
keytool -genkeypair -alias jogginapp -keyalg RSA -keysize 4096 -validity 3650 -dname "CN=localhost, OU=IESN, O=Henallux, L=Namur, C=BE" -keypass ChangeMe2026 -keystore src/main/resources/keystore.p12 -storetype PKCS12 -storepass ChangeMe2026 -ext "SAN=dns:localhost,ip:127.0.0.1"
```

## 5. Lancement

```bash
mvn spring-boot:run
```

Application disponible sur **https://localhost:8443/janvier**
(le navigateur avertit que le certificat est auto-signé : c'est attendu en développement).

Pour lancer en HTTP simple (démonstration sans TLS) : `SSL_ENABLED=false SERVER_PORT=8082 mvn spring-boot:run`

## 6. Tests

```bash
mvn test
```

## 7. Comptes de démonstration

| Login | Mot de passe | Rôles |
|---|---|---|
| `admin` | `AdminEshop2026!` | `ROLE_ADMIN`, `ROLE_USER` |
| `user1` | `MotDePasse2026!` | `ROLE_USER` |
| `user2` | `MotDePasse2026!` | `ROLE_USER` |

Les mots de passe sont stockés hachés en BCrypt.

---

## 8. Architecture

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

Les contrôleurs ne manipulent jamais d'entités JPA : la couche d'accès aux données
renvoie exclusivement des objets du paquet `model`.

## 9. Internationalisation

Trois niveaux, comme demandé au cahier des charges :

1. **Libellés statiques** : `messages_fr.properties` / `messages_en.properties`.
2. **Libellés dynamiques** (noms et descriptions venant de la base) : table **unique**
   `translations`, lue par **jointure** SQL filtrée sur la langue courante
   (voir `ProductRepository.SELECT_TRADUIT`).
3. **Messages d'erreur** : erreurs de validation et messages applicatifs traduits
   dans les mêmes fichiers de propriétés.

## 10. Promotions

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

## 11. Sécurité

| Mesure | Mise en œuvre |
|---|---|
| Authentification | Spring Security, `UserDetailsService` sur la table `users` |
| Mots de passe | Hachage **BCrypt** |
| Injections SQL | Requêtes paramétrées (Spring Data JPA / JPQL), aucune concaténation |
| CSRF | Protection active, jeton dans tous les formulaires POST |
| Cookies | `SameSite=Lax`, `HttpOnly`, `Secure` |
| TLS | HTTPS obligatoire + HSTS |
| CSP | `default-src 'self'` — aucun script ni style inline, aucun CDN |
| XSS | Nettoyage des saisies (OWASP HTML Sanitizer) + échappement `<c:out>` à l'affichage |
| Fixation de session | Nouvel identifiant de session à la connexion |
| Effets de bord | Aucune modification d'état en GET ; déconnexion en POST |
| Contrôle d'accès | Une commande n'est consultable et payable que par le client qui l'a passée |
