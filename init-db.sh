#!/usr/bin/env bash
#
# (Re)creation de la base de donnees du projet.
#
#   ./init-db.sh              -> demande confirmation si la base existe deja
#   ./init-db.sh --force      -> ne demande rien (attention : donnees perdues)
#   DB_NAME=autre ./init-db.sh
#
# Les tables et les donnees de test sont ensuite chargees automatiquement
# par l'application au demarrage (src/main/resources/schema.sql).
set -e

DB_NAME="${DB_NAME:-janvier_db}"
DB_USERNAME="${DB_USERNAME:-postgres}"
export PGPASSWORD="${DB_PASSWORD:-Ilies2004_}"
FORCE=0
[ "$1" = "--force" ] && FORCE=1

# --- Localiser les outils PostgreSQL -----------------------------------------
if ! command -v psql > /dev/null 2>&1; then
    for repertoire in /Library/PostgreSQL/18/bin /Library/PostgreSQL/16/bin \
                      /opt/homebrew/opt/postgresql@16/bin /opt/homebrew/bin /usr/local/bin; do
        if [ -x "$repertoire/psql" ]; then
            export PATH="$repertoire:$PATH"
            break
        fi
    done
fi

if ! command -v psql > /dev/null 2>&1; then
    echo "ERREUR : PostgreSQL introuvable. Installe-le ou ajoute psql au PATH."
    exit 1
fi

# --- Le serveur repond-il ? ---------------------------------------------------
if ! pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
    echo "ERREUR : PostgreSQL ne repond pas sur localhost:5432."
    echo "Demarre-le puis relance ce script (voir README.md, etape 1)."
    exit 1
fi

# --- Les identifiants sont-ils bons ? (-w : ne jamais demander interactivement)
if ! psql -U "$DB_USERNAME" -h localhost -w -d postgres -c '\q' > /dev/null 2>&1; then
    echo "ERREUR : connexion refusee pour l'utilisateur '$DB_USERNAME'."
    echo "Verifie DB_PASSWORD (valeur actuelle definie dans ce script ou l'environnement)."
    exit 1
fi

# --- Confirmation avant suppression ------------------------------------------
BASE_EXISTE=$(psql -U "$DB_USERNAME" -h localhost -w -d postgres -tAc \
    "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'")

if [ "$BASE_EXISTE" = "1" ] && [ "$FORCE" -eq 0 ]; then
    echo "La base '$DB_NAME' existe deja."
    echo "La recreer SUPPRIMERA definitivement son contenu (comptes crees, commandes...)."
    if [ ! -t 0 ]; then
        echo "Relance avec ./init-db.sh --force pour confirmer."
        exit 1
    fi
    printf "Continuer ? [o/N] "
    read -r reponse
    case "$reponse" in
        o|O|oui|OUI|y|Y|yes) ;;
        *) echo "Annule."; exit 1 ;;
    esac
fi

if [ "$BASE_EXISTE" = "1" ]; then
    echo "Suppression de la base '$DB_NAME'..."
    dropdb -U "$DB_USERNAME" -h localhost -w "$DB_NAME"
fi

echo "Creation de la base '$DB_NAME'..."
createdb -U "$DB_USERNAME" -h localhost -w -E UTF8 "$DB_NAME"

echo "Termine. Lance maintenant : ./run.sh"
