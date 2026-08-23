# JogginApp E-Shop

Projet IG338 de vente en ligne développé avec Java 17, Spring Boot, JSP,
Hibernate, PostgreSQL et Maven.

## Prérequis

- JDK 17
- PostgreSQL
- IntelliJ IDEA

## Préparer la base de données

Créer une base vide nommée `janvier_db` :

```bash
createdb -U postgres -h localhost janvier_db
```

Au premier démarrage, Spring Boot exécute automatiquement
`src/main/resources/schema.sql`. Ce fichier crée les tables et insère les
données de démonstration.

Si une ancienne version de la base existe, la recréer avant la démonstration :

```bash
dropdb -U postgres -h localhost janvier_db
createdb -U postgres -h localhost janvier_db
```

## Lancer dans IntelliJ

Avant le lancement, vérifier que le service PostgreSQL est démarré et que la
base `janvier_db` existe. Cette préparation n'est nécessaire qu'une fois.

1. Ouvrir le projet dans IntelliJ.
2. Vérifier que le Project SDK est le JDK 17.
3. Ouvrir `src/main/java/be/henallux/janvier/JanvierProjectApplication.java`.
4. Cliquer sur le triangle vert à côté de `main`, puis sur **Run**.
5. Ouvrir `https://localhost:8443/janvier`.

Le certificat TLS est auto-signé. L’avertissement du navigateur en local est
donc normal.

Par défaut, PostgreSQL est contacté avec l’utilisateur `postgres` et le mot de
passe `postgres`. Si nécessaire, définir les variables suivantes dans la
configuration Run d’IntelliJ :

| Variable | Utilisation |
|---|---|
| `DB_URL` | URL JDBC PostgreSQL |
| `DB_USERNAME` | Utilisateur PostgreSQL |
| `DB_PASSWORD` | Mot de passe PostgreSQL |
| `PAYPAL_CLIENT_ID` | Identifiant de l’application PayPal sandbox |
| `PAYPAL_CLIENT_SECRET` | Secret de l’application PayPal sandbox |
| `PAYPAL_MODE` | `sandbox` par défaut |

Le paiement nécessite des identifiants PayPal sandbox. Le reste de
l’application peut être lancé sans ces identifiants.

## Comptes de démonstration

| Login | Mot de passe | Rôle |
|---|---|---|
| `user1` | `MotDePasse2026!` | `ROLE_USER` |
| `user2` | `MotDePasse2026!` | `ROLE_USER` |

Un nouveau compte peut également être créé depuis le formulaire d’inscription.

## Tests

```bash
mvn test
```

Les tests utilisent JUnit et Mockito et ne nécessitent pas de base de données.

## Contenu nécessaire au fonctionnement

- `src/main/java` : contrôleurs, services, modèles et accès aux données
- `src/main/webapp` : JSP, template Tiles, CSS, JavaScript et images
- `src/main/resources/schema.sql` : création et population de la base
- `src/main/resources/messages*.properties` : traductions françaises et anglaises
- `src/main/resources/keystore.p12` : certificat TLS local
- `pom.xml` : dépendances et construction Maven
