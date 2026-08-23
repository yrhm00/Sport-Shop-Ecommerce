# JogginApp E-Shop

Application Java 17 / Spring Boot utilisant PostgreSQL.

## 1. Installer les prérequis

- IntelliJ IDEA
- JDK 17
- PostgreSQL

## 2. Démarrer PostgreSQL

Sur le Mac utilisé pour le projet :

```bash
sudo -u postgres /Library/PostgreSQL/18/bin/pg_ctl -D /Library/PostgreSQL/18/data start
```

Vérifier que PostgreSQL répond :

```bash
/Library/PostgreSQL/18/bin/pg_isready -h localhost -p 5432
```

Le résultat attendu contient `accepting connections`. Si PostgreSQL est déjà
lancé, passer directement à l’étape suivante.

Sur un autre ordinateur, démarrer simplement le service PostgreSQL installé
(Services Windows, `systemctl` sous Linux ou l’application PostgreSQL sous
macOS).

## 3. Créer la base de données

Cette commande n’est nécessaire que la première fois :

```bash
/Library/PostgreSQL/18/bin/createdb -U postgres -h localhost janvier_db
```

Entrer le mot de passe PostgreSQL demandé. Si la commande indique que la base
existe déjà, il n’y a rien à refaire.

Il ne faut importer aucun fichier SQL manuellement. Au démarrage, l’application
exécute automatiquement `src/main/resources/schema.sql` pour créer les tables
et insérer les données de démonstration.

Pour repartir exceptionnellement d’une base vide, les commandes suivantes
suppriment toutes les données puis recréent la base :

```bash
/Library/PostgreSQL/18/bin/dropdb -U postgres -h localhost janvier_db
/Library/PostgreSQL/18/bin/createdb -U postgres -h localhost janvier_db
```

## 4. Ouvrir le projet dans IntelliJ

1. Ouvrir IntelliJ IDEA.
2. Choisir **Open** et sélectionner le dossier `Sport-Shop-Ecommerce`.
3. Attendre la fin du chargement des dépendances Maven.
4. Aller dans **File > Project Structure > Project**.
5. Sélectionner le **Project SDK 17** et le niveau de langage Java 17.

## 5. Configurer le lancement

Ouvrir :

`src/main/java/be/henallux/janvier/JanvierProjectApplication.java`

Cliquer sur le triangle vert à côté de la méthode `main`, puis sur **Modify Run
Configuration** si la configuration PostgreSQL doit être adaptée.

Dans **Environment variables**, définir au minimum le mot de passe réellement
choisi lors de l’installation de PostgreSQL :

```text
DB_URL=jdbc:postgresql://localhost:5432/janvier_db;DB_USERNAME=postgres;DB_PASSWORD=VOTRE_MOT_DE_PASSE
```

Si le mot de passe PostgreSQL est `postgres`, aucune variable n’est nécessaire :
ce sont déjà les valeurs par défaut du projet.

Pour tester le paiement PayPal, ajouter également :

```text
PAYPAL_CLIENT_ID=VOTRE_CLIENT_ID;PAYPAL_CLIENT_SECRET=VOTRE_SECRET;PAYPAL_MODE=sandbox
```

L’application peut démarrer sans ces trois variables, mais le paiement PayPal
ne pourra pas être effectué.

## 6. Lancer l’application

1. Cliquer sur le triangle vert de `JanvierProjectApplication`.
2. Choisir **Run 'JanvierProjectApplication'**.
3. Attendre le message indiquant que l’application a démarré.
4. Ouvrir [https://localhost:8443/janvier](https://localhost:8443/janvier).

Le certificat HTTPS est auto-signé pour le développement. L’avertissement du
navigateur est donc normal : choisir l’option avancée permettant de continuer
vers le site local.

## 7. Comptes de démonstration

| Login | Mot de passe |
|---|---|
| `user1` | `MotDePasse2026!` |
| `user2` | `MotDePasse2026!` |

Un nouveau compte peut aussi être créé depuis le formulaire d’inscription.

## 8. Lancer les tests

Dans IntelliJ, ouvrir la fenêtre **Maven**, puis exécuter
**Lifecycle > test**. Il est aussi possible d’utiliser le terminal :

```bash
mvn test
```

## Problèmes fréquents

- **Connection refused** : PostgreSQL n’est pas démarré.
- **Password authentication failed** : corriger `DB_PASSWORD` dans la
  configuration Run d’IntelliJ.
- **Database janvier_db does not exist** : exécuter la commande `createdb` de
  l’étape 3.
- **Port 8443 already in use** : arrêter l’ancien lancement de l’application
  avec le carré rouge d’IntelliJ, puis relancer.
