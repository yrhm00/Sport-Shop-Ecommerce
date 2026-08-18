#!/usr/bin/env bash
#
# Lancement de l'application JogginApp E-Shop.
#
#   ./run.sh                              -> https://localhost:8443/janvier
#   SSL_ENABLED=false SERVER_PORT=8082 ./run.sh   -> http://localhost:8082/janvier
#
# Prerequis : PostgreSQL demarre et base 'janvier_db' creee (voir README.md).
set -e

cd "$(dirname "$0")"

# --- JDK 17 (le projet ne compile pas avec un JDK plus recent) ---------------
if [ -z "$JAVA_HOME" ] || ! "$JAVA_HOME/bin/java" -version 2>&1 | grep -q '"17'; then
    if command -v /usr/libexec/java_home > /dev/null 2>&1; then
        JAVA_HOME_17=$(/usr/libexec/java_home -v 17 2>/dev/null || true)
        if [ -n "$JAVA_HOME_17" ]; then
            export JAVA_HOME="$JAVA_HOME_17"
        fi
    fi
fi

if ! "${JAVA_HOME:-/usr}/bin/java" -version 2>&1 | grep -q '"17'; then
    echo "ERREUR : JDK 17 introuvable."
    echo "Installe-le (macOS : brew install openjdk@17) puis relance."
    exit 1
fi
echo "JDK utilise : $JAVA_HOME"

# --- Base de donnees ---------------------------------------------------------
export DB_URL="${DB_URL:-jdbc:postgresql://localhost:5432/janvier_db?useUnicode=true&characterEncoding=UTF-8}"
export DB_USERNAME="${DB_USERNAME:-postgres}"
export DB_PASSWORD="${DB_PASSWORD:-Ilies2004_}"

# --- PayPal (identifiants sandbox du projet : aucun argent reel) -------------
export PAYPAL_CLIENT_ID="${PAYPAL_CLIENT_ID:-ASXWWhLhd_mx15AtVETBFrxq-4KsZicGJnon4ppqKqapddF8Ugkov7T4zRZNhtCWm5-szWaVu3Sj3ive}"
export PAYPAL_CLIENT_SECRET="${PAYPAL_CLIENT_SECRET:-EKXVHcJ9TfPvJn08uuhEQZEkviCVHL7OXLK0YFRkUDNg1ozIXjL_yKlB6GWPXo99QzgMDvP_OBLcVjnr}"
export PAYPAL_MODE="${PAYPAL_MODE:-sandbox}"

# --- Serveur -----------------------------------------------------------------
export SSL_ENABLED="${SSL_ENABLED:-true}"
export SERVER_PORT="${SERVER_PORT:-8443}"
export SSL_KEYSTORE_PASSWORD="${SSL_KEYSTORE_PASSWORD:-ChangeMe2026}"

if [ "$SSL_ENABLED" = "true" ]; then
    echo "Application : https://localhost:${SERVER_PORT}/janvier"
    echo "(certificat auto-signe : accepter l'avertissement du navigateur)"
else
    echo "Application : http://localhost:${SERVER_PORT}/janvier"
fi

mvn spring-boot:run
