-- ============================================
-- SCHEMA SQL - PROJET SEPTEMBRE 2026
-- Site de vente en ligne - JogginApp E-Shop
-- ============================================
-- Script idempotent : peut etre rejoue sans erreur.

-- ============================================
-- TABLE: USERS (Utilisateurs/Clients)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL, -- BCrypt hash (60 caracteres)
    enabled BOOLEAN DEFAULT TRUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    adresse TEXT NOT NULL,
    code_postal VARCHAR(10),
    localite VARCHAR(100)
);

-- ============================================
-- TABLE: AUTHORITIES (Roles et permissions)
-- ============================================
CREATE TABLE IF NOT EXISTS authorities (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE,
    UNIQUE(username, authority)
);

-- ============================================
-- TABLE: CATEGORIES (Categories de produits)
-- ============================================
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    image_url VARCHAR(255)
);

-- ============================================
-- TABLE: PRODUCTS (Produits)
-- ============================================
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(150) NOT NULL,
    description TEXT,
    prix DECIMAL(10,2) NOT NULL,
    stock INTEGER DEFAULT 0,
    image_url VARCHAR(255),
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- ============================================
-- TABLE: PRODUCT_SIZES (Stock par taille)
-- ============================================
CREATE TABLE IF NOT EXISTS product_sizes (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    taille VARCHAR(10) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE(product_id, taille)
);

-- ============================================
-- TABLE: TRANSLATIONS (i18n dynamique - TABLE UNIQUE)
-- --------------------------------------------
-- Une seule table pour toutes les traductions de la base.
-- entity_type : 'CATEGORY' ou 'PRODUCT'
-- champ       : 'nom' ou 'description'
-- La selection de la bonne ligne se fait par JOINTURE dans les
-- requetes (voir ProductRepository / CategoryRepository), et non
-- par un filtrage en Java.
-- ============================================
CREATE TABLE IF NOT EXISTS translations (
    id SERIAL PRIMARY KEY,
    entity_type VARCHAR(20) NOT NULL,
    entity_id INTEGER NOT NULL,
    locale VARCHAR(5) NOT NULL,
    champ VARCHAR(30) NOT NULL,
    valeur TEXT NOT NULL,
    UNIQUE(entity_type, entity_id, locale, champ)
);

CREATE INDEX IF NOT EXISTS idx_translations_lookup
    ON translations (entity_type, entity_id, locale);

-- ============================================
-- TABLE: PROMOTIONS (regles de promotion parametrables)
-- --------------------------------------------
-- portee    : 'PRODUIT'   -> s'applique au produit product_id
--             'CATEGORIE' -> s'applique aux produits de category_id
--             'PANIER'    -> s'applique au total du panier
-- type_reduction : 'POURCENTAGE' (valeur = 10 => -10%)
--                  'MONTANT'     (valeur = 5.00 => -5,00 EUR)
-- seuil_min : montant minimum du panier pour declencher une promo PANIER
-- Modifier une promotion = un UPDATE ici, aucun code a recompiler.
-- ============================================
CREATE TABLE IF NOT EXISTS promotions (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    libelle VARCHAR(150) NOT NULL,
    portee VARCHAR(20) NOT NULL,
    type_reduction VARCHAR(20) NOT NULL,
    valeur DECIMAL(10,2) NOT NULL,
    seuil_min DECIMAL(10,2),
    product_id INTEGER,
    category_id INTEGER,
    date_debut TIMESTAMP,
    date_fin TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- ============================================
-- TABLE: ORDERS (Commandes)
-- --------------------------------------------
-- statut : 'EN_ATTENTE' (enregistree avant paiement)
--          'PAYEE'      (paiement PayPal capture)
--          'ANNULEE'    (abandon du paiement par le client)
-- La commande n'est JAMAIS supprimee : la trace reste en base.
-- ============================================
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    montant_total DECIMAL(10,2) NOT NULL,
    montant_reduction DECIMAL(10,2) NOT NULL DEFAULT 0,
    paye BOOLEAN DEFAULT FALSE,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    paypal_order_id VARCHAR(64),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Compatibilite avec une base creee avant l'ajout du rattachement PayPal.
ALTER TABLE orders ADD COLUMN IF NOT EXISTS paypal_order_id VARCHAR(64);
CREATE UNIQUE INDEX IF NOT EXISTS idx_orders_paypal_order_id
    ON orders (paypal_order_id) WHERE paypal_order_id IS NOT NULL;

-- ============================================
-- TABLE: ORDER_LINES (Lignes de commande)
-- ============================================
CREATE TABLE IF NOT EXISTS order_lines (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    taille VARCHAR(10),
    quantite INTEGER NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- ============================================
-- DONNEES DE TEST
-- ============================================

-- USERS
-- Mot de passe des 2 comptes de demonstration : MotDePasse2026!
-- (hash BCrypt genere avec BCryptPasswordEncoder, 60 caracteres)
INSERT INTO users (username, password, enabled, nom, prenom, email, telephone, adresse, code_postal, localite) VALUES
('user1', '$2a$10$Y1u5d7/RPZdz9GORqboke.2nUSpn32EhF2SzLIx/U8eSR5.t3LhhG', TRUE, 'Dupont', 'Jean', 'jean.dupont@email.be', '0472345678', '10 Avenue des Tilleuls', '4000', 'Liege'),
('user2', '$2a$10$Y1u5d7/RPZdz9GORqboke.2nUSpn32EhF2SzLIx/U8eSR5.t3LhhG', TRUE, 'Martin', 'Sophie', 'sophie.martin@email.be', '0473456789', '25 Rue du Commerce', '6000', 'Charleroi')
ON CONFLICT (username) DO NOTHING;

-- AUTHORITIES
INSERT INTO authorities (username, authority) VALUES
('user1', 'ROLE_USER'),
('user2', 'ROLE_USER')
ON CONFLICT (username, authority) DO NOTHING;

-- CATEGORIES (libelles en francais dans la table, traductions dans 'translations')
INSERT INTO categories (code, nom, image_url) VALUES
('CHAUSSURES', 'Chaussures de Running', '/images/categories/shoes.png'),
('VETEMENTS', 'Vetements Techniques', '/images/categories/clothes.png'),
('ACCESSOIRES', 'Accessoires', '/images/categories/accessories.png'),
('MONTRES', 'Montres et Trackers', '/images/categories/watches.png')
ON CONFLICT (code) DO NOTHING;

-- PRODUCTS
INSERT INTO products (category_id, code, nom, description, prix, stock, image_url) VALUES
((SELECT id FROM categories WHERE code='CHAUSSURES'), 'SHOE-001', 'Nike Air Zoom Pegasus', 'Chaussure polyvalente pour tous types de courses', 129.99, 50, '/images/products/nike_pegasus.png'),
((SELECT id FROM categories WHERE code='CHAUSSURES'), 'SHOE-002', 'Adidas Ultraboost', 'Amorti maximal et retour d''energie optimal', 159.99, 50, '/images/products/adidas_ultraboost.png'),
((SELECT id FROM categories WHERE code='CHAUSSURES'), 'SHOE-003', 'Asics Gel-Kayano', 'Stabilite et confort pour longues distances', 149.99, 50, '/images/products/asics_gel_kayano.png'),
((SELECT id FROM categories WHERE code='CHAUSSURES'), 'SHOE-004', 'New Balance 880', 'Equilibre parfait entre amorti et reactivite', 119.99, 50, '/images/products/new_balance_880.png'),
((SELECT id FROM categories WHERE code='VETEMENTS'), 'CLOTH-001', 'T-shirt Running Nike Dri-FIT', 'Evacuation de la transpiration', 39.99, 80, '/images/products/nike_tshirt.png'),
((SELECT id FROM categories WHERE code='VETEMENTS'), 'CLOTH-002', 'Short Adidas Own The Run', 'Leger et respirant', 34.99, 80, '/images/products/adidas_shorts.png'),
((SELECT id FROM categories WHERE code='VETEMENTS'), 'CLOTH-003', 'Veste coupe-vent Gore-Tex', 'Protection contre le vent et la pluie', 89.99, 80, '/images/products/gore_tex_jacket.png'),
((SELECT id FROM categories WHERE code='VETEMENTS'), 'CLOTH-004', 'Legging compression Under Armour', 'Support musculaire optimal', 49.99, 80, '/images/products/under_armour_legging.png'),
((SELECT id FROM categories WHERE code='ACCESSOIRES'), 'ACC-001', 'Bouteille isotherme CamelBak', 'Garde l''eau fraiche pendant 24h', 24.99, 120, '/images/products/camelbak_bottle.png'),
((SELECT id FROM categories WHERE code='ACCESSOIRES'), 'ACC-002', 'Sac a dos running Salomon', 'Hydratation integree', 79.99, 45, '/images/products/salomon_backpack.png'),
((SELECT id FROM categories WHERE code='ACCESSOIRES'), 'ACC-003', 'Casquette Nike AeroBill', 'Protection solaire et legerete', 29.99, 90, '/images/products/nike_cap.png'),
((SELECT id FROM categories WHERE code='ACCESSOIRES'), 'ACC-004', 'Ceinture porte-bidon FlipBelt', 'Discrete et confortable', 34.99, 55, '/images/products/flipbelt.png'),
((SELECT id FROM categories WHERE code='MONTRES'), 'WATCH-001', 'Garmin Forerunner 255', 'GPS et cardiofrequencemetre integre', 349.99, 25, '/images/products/garmin_forerunner_255.png'),
((SELECT id FROM categories WHERE code='MONTRES'), 'WATCH-002', 'Polar Vantage V2', 'Analyse avancee de la performance', 499.99, 15, '/images/products/polar_vantage_v2.png'),
((SELECT id FROM categories WHERE code='MONTRES'), 'WATCH-003', 'Apple Watch SE', 'Fitness et sante au quotidien', 279.99, 40, '/images/products/apple_watch_se.png')
ON CONFLICT (code) DO NOTHING;

-- TRADUCTIONS DES CATEGORIES (table unique)
INSERT INTO translations (entity_type, entity_id, locale, champ, valeur) VALUES
('CATEGORY', (SELECT id FROM categories WHERE code='CHAUSSURES'), 'fr', 'nom', 'Chaussures de Running'),
('CATEGORY', (SELECT id FROM categories WHERE code='CHAUSSURES'), 'en', 'nom', 'Running Shoes'),
('CATEGORY', (SELECT id FROM categories WHERE code='VETEMENTS'), 'fr', 'nom', 'Vêtements Techniques'),
('CATEGORY', (SELECT id FROM categories WHERE code='VETEMENTS'), 'en', 'nom', 'Technical Clothing'),
('CATEGORY', (SELECT id FROM categories WHERE code='ACCESSOIRES'), 'fr', 'nom', 'Accessoires'),
('CATEGORY', (SELECT id FROM categories WHERE code='ACCESSOIRES'), 'en', 'nom', 'Accessories'),
('CATEGORY', (SELECT id FROM categories WHERE code='MONTRES'), 'fr', 'nom', 'Montres et Trackers'),
('CATEGORY', (SELECT id FROM categories WHERE code='MONTRES'), 'en', 'nom', 'Watches and Trackers')
ON CONFLICT (entity_type, entity_id, locale, champ) DO NOTHING;

-- TRADUCTIONS DES PRODUITS (table unique)
INSERT INTO translations (entity_type, entity_id, locale, champ, valeur) VALUES
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-001'), 'fr', 'nom', 'Nike Air Zoom Pegasus'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-001'), 'fr', 'description', 'Chaussure polyvalente pour tous types de courses'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-001'), 'en', 'nom', 'Nike Air Zoom Pegasus'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-001'), 'en', 'description', 'Versatile shoe for all types of running'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-002'), 'fr', 'nom', 'Adidas Ultraboost'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-002'), 'fr', 'description', 'Amorti maximal et retour d''énergie optimal'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-002'), 'en', 'nom', 'Adidas Ultraboost'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-002'), 'en', 'description', 'Maximum cushioning and optimal energy return'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-003'), 'fr', 'nom', 'Asics Gel-Kayano'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-003'), 'fr', 'description', 'Stabilité et confort pour longues distances'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-003'), 'en', 'nom', 'Asics Gel-Kayano'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-003'), 'en', 'description', 'Stability and comfort for long distances'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-004'), 'fr', 'nom', 'New Balance 880'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-004'), 'fr', 'description', 'Équilibre parfait entre amorti et réactivité'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-004'), 'en', 'nom', 'New Balance 880'),
('PRODUCT', (SELECT id FROM products WHERE code='SHOE-004'), 'en', 'description', 'Perfect balance between cushioning and responsiveness'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-001'), 'fr', 'nom', 'T-shirt Running Nike Dri-FIT'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-001'), 'fr', 'description', 'Évacuation de la transpiration'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-001'), 'en', 'nom', 'Nike Dri-FIT Running T-shirt'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-001'), 'en', 'description', 'Moisture wicking'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-002'), 'fr', 'nom', 'Short Adidas Own The Run'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-002'), 'fr', 'description', 'Léger et respirant'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-002'), 'en', 'nom', 'Adidas Own The Run Shorts'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-002'), 'en', 'description', 'Light and breathable'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-003'), 'fr', 'nom', 'Veste coupe-vent Gore-Tex'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-003'), 'fr', 'description', 'Protection contre le vent et la pluie'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-003'), 'en', 'nom', 'Gore-Tex Windbreaker'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-003'), 'en', 'description', 'Wind and rain protection'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-004'), 'fr', 'nom', 'Legging compression Under Armour'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-004'), 'fr', 'description', 'Support musculaire optimal'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-004'), 'en', 'nom', 'Under Armour Compression Leggings'),
('PRODUCT', (SELECT id FROM products WHERE code='CLOTH-004'), 'en', 'description', 'Optimal muscle support'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-001'), 'fr', 'nom', 'Bouteille isotherme CamelBak'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-001'), 'fr', 'description', 'Garde l''eau fraîche pendant 24h'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-001'), 'en', 'nom', 'CamelBak Insulated Bottle'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-001'), 'en', 'description', 'Keeps water cold for 24h'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-002'), 'fr', 'nom', 'Sac à dos running Salomon'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-002'), 'fr', 'description', 'Hydratation intégrée'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-002'), 'en', 'nom', 'Salomon Running Backpack'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-002'), 'en', 'description', 'Integrated hydration'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-003'), 'fr', 'nom', 'Casquette Nike AeroBill'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-003'), 'fr', 'description', 'Protection solaire et légèreté'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-003'), 'en', 'nom', 'Nike AeroBill Cap'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-003'), 'en', 'description', 'Sun protection and lightness'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-004'), 'fr', 'nom', 'Ceinture porte-bidon FlipBelt'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-004'), 'fr', 'description', 'Discrète et confortable'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-004'), 'en', 'nom', 'FlipBelt Running Belt'),
('PRODUCT', (SELECT id FROM products WHERE code='ACC-004'), 'en', 'description', 'Discreet and comfortable'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-001'), 'fr', 'nom', 'Garmin Forerunner 255'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-001'), 'fr', 'description', 'GPS et cardiofréquencemètre intégré'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-001'), 'en', 'nom', 'Garmin Forerunner 255'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-001'), 'en', 'description', 'GPS and integrated heart rate monitor'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-002'), 'fr', 'nom', 'Polar Vantage V2'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-002'), 'fr', 'description', 'Analyse avancée de la performance'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-002'), 'en', 'nom', 'Polar Vantage V2'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-002'), 'en', 'description', 'Advanced performance analysis'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-003'), 'fr', 'nom', 'Apple Watch SE'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-003'), 'fr', 'description', 'Fitness et santé au quotidien'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-003'), 'en', 'nom', 'Apple Watch SE'),
('PRODUCT', (SELECT id FROM products WHERE code='WATCH-003'), 'en', 'description', 'Daily fitness and health')
ON CONFLICT (entity_type, entity_id, locale, champ) DO NOTHING;

-- PRODUCT SIZES
INSERT INTO product_sizes (product_id, taille, stock)
SELECT p.id, t.taille, t.stock
FROM products p
JOIN (VALUES ('40', 10), ('41', 10), ('42', 10), ('43', 10), ('44', 10)) AS t(taille, stock) ON TRUE
WHERE p.code IN ('SHOE-001', 'SHOE-002', 'SHOE-003', 'SHOE-004')
ON CONFLICT (product_id, taille) DO NOTHING;

INSERT INTO product_sizes (product_id, taille, stock)
SELECT p.id, t.taille, t.stock
FROM products p
JOIN (VALUES ('S', 20), ('M', 20), ('L', 20), ('XL', 20)) AS t(taille, stock) ON TRUE
WHERE p.code IN ('CLOTH-001', 'CLOTH-002', 'CLOTH-003', 'CLOTH-004')
ON CONFLICT (product_id, taille) DO NOTHING;

-- PROMOTIONS (parametrables : modifier une ligne suffit a changer la promo du site)
INSERT INTO promotions (code, libelle, portee, type_reduction, valeur, seuil_min, product_id, category_id, date_debut, date_fin, active) VALUES
('PROMO-MONTRES', 'Promotion sur les montres et trackers', 'CATEGORIE', 'POURCENTAGE', 15.00, NULL, NULL, (SELECT id FROM categories WHERE code='MONTRES'), NULL, NULL, TRUE),
('PROMO-ULTRABOOST', 'Offre speciale Adidas Ultraboost', 'PRODUIT', 'POURCENTAGE', 10.00, NULL, (SELECT id FROM products WHERE code='SHOE-002'), NULL, NULL, NULL, TRUE),
('PROMO-CAMELBAK', 'Bouteille CamelBak : 5 EUR de remise', 'PRODUIT', 'MONTANT', 5.00, NULL, (SELECT id FROM products WHERE code='ACC-001'), NULL, NULL, NULL, TRUE),
('PROMO-PANIER-100', 'Remise de 10% des 100 EUR d''achat', 'PANIER', 'POURCENTAGE', 10.00, 100.00, NULL, NULL, NULL, NULL, TRUE)
ON CONFLICT (code) DO NOTHING;
