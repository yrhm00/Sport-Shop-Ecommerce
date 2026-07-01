
-- ============================================
-- SCHEMA SQL - PROJET JANVIER 2026
-- Site de vente en ligne - E-Shop
-- ============================================

-- NOTE: Suppression des DROP pour persistance (Quick fix)
-- On utilise CREATE TABLE IF NOT EXISTS

-- ============================================
-- TABLE: USERS (Utilisateurs/Clients)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL, -- BCrypt hash (60 caractères)
    enabled BOOLEAN DEFAULT TRUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    adresse TEXT NOT NULL,
    code_postal VARCHAR(10),
    localite VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLE: AUTHORITIES (Rôles et permissions)
-- ============================================
CREATE TABLE IF NOT EXISTS authorities (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE,
    UNIQUE(username, authority)
);

-- ============================================
-- TABLE: CATEGORIES (Catégories de produits)
-- ============================================
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    image_url VARCHAR(255)
);

-- ============================================
-- TABLE: CATEGORY_TRANSLATIONS (i18n dynamique)
-- ============================================
CREATE TABLE IF NOT EXISTS category_translations (
    id SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL,
    locale VARCHAR(5) NOT NULL, -- 'fr', 'en'
    nom_traduit VARCHAR(100) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE(category_id, locale)
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- ============================================
-- TABLE: PRODUCT_TRANSLATIONS (i18n dynamique)
-- ============================================
CREATE TABLE IF NOT EXISTS product_translations (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    locale VARCHAR(5) NOT NULL,
    nom_traduit VARCHAR(150) NOT NULL,
    description_traduite TEXT,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE(product_id, locale)
);

-- ============================================
-- TABLE: ORDERS (Commandes)
-- ============================================
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    montant_total DECIMAL(10,2) NOT NULL,
    paye BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- TABLE: ORDER_LINES (Lignes de commande)
-- ============================================
CREATE TABLE IF NOT EXISTS order_lines (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantite INTEGER NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================
-- TABLE: PRODUCT_SIZES (Stock par taille)
-- ============================================
CREATE TABLE IF NOT EXISTS product_sizes (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    taille VARCHAR(10) NOT NULL,
    stock INTEGER DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE(product_id, taille)
);

-- ============================================
-- DONNÉES DE TEST
-- ============================================

-- USERS
-- Hash BCrypt pour "password" : $2a$10$N9qo8uLOickgx2ZMRZoMye/Jo8oIxdpEoGb8rJRJF6vXKJf1E/6b2
INSERT INTO users (username, password, enabled, nom, prenom, email, telephone, adresse, code_postal, localite) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Jo8oIxdpEoGb8rJRJF6vXKJf1E/6b2', TRUE, 'Admin', 'Super', 'admin@eshop.be', '0471234567', '1 Rue de la Paix', '5000', 'Namur'),
('user1', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Jo8oIxdpEoGb8rJRJF6vXKJf1E/6b2', TRUE, 'Dupont', 'Jean', 'jean.dupont@email.be', '0472345678', '10 Avenue des Tilleuls', '4000', 'Liège'),
('user2', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Jo8oIxdpEoGb8rJRJF6vXKJf1E/6b2', TRUE, 'Martin', 'Sophie', 'sophie.martin@email.be', '0473456789', '25 Rue du Commerce', '6000', 'Charleroi')
ON CONFLICT (username) DO NOTHING;

-- AUTHORITIES
INSERT INTO authorities (username, authority) VALUES
('admin', 'ROLE_ADMIN'),
('admin', 'ROLE_USER'),
('user1', 'ROLE_USER'),
('user2', 'ROLE_USER')
ON CONFLICT (username, authority) DO NOTHING;

-- CATEGORIES
INSERT INTO categories (code, nom, image_url) VALUES
('CHAUSSURES', 'Chaussures de Running', '/images/categories/shoes.png'),
('VETEMENTS', 'Vêtements Techniques', '/images/categories/clothes.png'),
('ACCESSOIRES', 'Accessoires', '/images/categories/accessories.png'),
('MONTRES', 'Montres et Trackers', '/images/categories/watches.png')
ON CONFLICT (code) DO NOTHING;

-- CATEGORY TRANSLATIONS
INSERT INTO category_translations (category_id, locale, nom_traduit) VALUES
(1, 'fr', 'Chaussures de Running'),
(1, 'en', 'Running Shoes'),
(2, 'fr', 'Vêtements Techniques'),
(2, 'en', 'Technical Clothing'),
(3, 'fr', 'Accessoires'),
(3, 'en', 'Accessories'),
(4, 'fr', 'Montres et Trackers'),
(4, 'en', 'Watches and Trackers')
ON CONFLICT (category_id, locale) DO NOTHING;

-- PRODUCTS (Chaussures)
INSERT INTO products (category_id, code, nom, description, prix, stock, image_url) VALUES
(1, 'SHOE-001', 'Nike Air Zoom Pegasus', 'Chaussure polyvalente pour tous types de courses', 129.99, 50, '/images/products/nike_pegasus.png'),
(1, 'SHOE-002', 'Adidas Ultraboost', 'Amorti maximal et retour d''énergie optimal', 159.99, 30, '/images/products/adidas_ultraboost.png'),
(1, 'SHOE-003', 'Asics Gel-Kayano', 'Stabilité et confort pour longues distances', 149.99, 40, '/images/products/asics_gel_kayano.png'),
(1, 'SHOE-004', 'New Balance 880', 'Équilibre parfait entre amorti et réactivité', 119.99, 60, '/images/products/new_balance_880.png')
ON CONFLICT (code) DO NOTHING;

-- PRODUCTS (Vêtements)
INSERT INTO products (category_id, code, nom, description, prix, stock, image_url) VALUES
(2, 'CLOTH-001', 'T-shirt Running Nike Dri-FIT', 'Évacuation de la transpiration', 39.99, 100, '/images/products/nike_tshirt.png'),
(2, 'CLOTH-002', 'Short Adidas Own The Run', 'Léger et respirant', 34.99, 80, '/images/products/adidas_shorts.png'),
(2, 'CLOTH-003', 'Veste coupe-vent Gore-Tex', 'Protection contre le vent et la pluie', 89.99, 35, '/images/products/gore_tex_jacket.png'),
(2, 'CLOTH-004', 'Legging compression Under Armour', 'Support musculaire optimal', 49.99, 70, '/images/products/under_armour_legging.png')
ON CONFLICT (code) DO NOTHING;

-- PRODUCTS (Accessoires)
INSERT INTO products (category_id, code, nom, description, prix, stock, image_url) VALUES
(3, 'ACC-001', 'Bouteille isotherme CamelBak', 'Garde l''eau fraîche pendant 24h', 24.99, 120, '/images/products/camelbak_bottle.png'),
(3, 'ACC-002', 'Sac à dos running Salomon', 'Hydratation intégrée', 79.99, 45, '/images/products/salomon_backpack.png'),
(3, 'ACC-003', 'Casquette Nike AeroBill', 'Protection solaire et légèreté', 29.99, 90, '/images/products/nike_cap.png'),
(3, 'ACC-004', 'Ceinture porte-bidon FlipBelt', 'Discrète et confortable', 34.99, 55, '/images/products/flipbelt.png')
ON CONFLICT (code) DO NOTHING;

-- PRODUCTS (Montres)
INSERT INTO products (category_id, code, nom, description, prix, stock, image_url) VALUES
(4, 'WATCH-001', 'Garmin Forerunner 255', 'GPS et cardiofréquencemètre intégré', 349.99, 25, '/images/products/garmin_forerunner_255.png'),
(4, 'WATCH-002', 'Polar Vantage V2', 'Analyse avancée de la performance', 499.99, 15, '/images/products/polar_vantage_v2.png'),
(4, 'WATCH-003', 'Apple Watch SE', 'Fitness et santé au quotidien', 279.99, 40, '/images/products/apple_watch_se.png')
ON CONFLICT (code) DO NOTHING;

-- PRODUCT TRANSLATIONS
INSERT INTO product_translations (product_id, locale, nom_traduit, description_traduite) VALUES
-- Chaussures
(1, 'fr', 'Nike Air Zoom Pegasus', 'Chaussure polyvalente pour tous types de courses'),
(1, 'en', 'Nike Air Zoom Pegasus', 'Versatile shoe for all types of running'),
(2, 'fr', 'Adidas Ultraboost', 'Amorti maximal et retour d''énergie optimal'),
(2, 'en', 'Adidas Ultraboost', 'Maximum cushioning and optimal energy return'),
(3, 'fr', 'Asics Gel-Kayano', 'Stabilité et confort pour longues distances'),
(3, 'en', 'Asics Gel-Kayano', 'Stability and comfort for long distances'),
(4, 'fr', 'New Balance 880', 'Équilibre parfait entre amorti et réactivité'),
(4, 'en', 'New Balance 880', 'Perfect balance between cushioning and responsiveness'),
-- Vêtements
(5, 'fr', 'T-shirt Running Nike Dri-FIT', 'Évacuation de la transpiration'),
(5, 'en', 'Nike Dri-FIT Running T-shirt', 'Moisture wicking'),
(6, 'fr', 'Short Adidas Own The Run', 'Léger et respirant'),
(6, 'en', 'Adidas Own The Run Shorts', 'Light and breathable'),
(7, 'fr', 'Veste coupe-vent Gore-Tex', 'Protection contre le vent et la pluie'),
(7, 'en', 'Gore-Tex Windbreaker', 'Wind and rain protection'),
(8, 'fr', 'Legging compression Under Armour', 'Support musculaire optimal'),
(8, 'en', 'Under Armour Compression Leggings', 'Optimal muscle support'),
-- Accessoires
(9, 'fr', 'Bouteille isotherme CamelBak', 'Garde l''eau fraîche pendant 24h'),
(9, 'en', 'CamelBak Insulated Bottle', 'Keeps water cold for 24h'),
(10, 'fr', 'Sac à dos running Salomon', 'Hydratation intégrée'),
(10, 'en', 'Salomon Running Backpack', 'Integrated hydration'),
(11, 'fr', 'Casquette Nike AeroBill', 'Protection solaire et légèreté'),
(11, 'en', 'Nike AeroBill Cap', 'Sun protection and lightness'),
(12, 'fr', 'Ceinture porte-bidon FlipBelt', 'Discrète et confortable'),
(12, 'en', 'FlipBelt Running Belt', 'Discreet and comfortable'),
-- Montres
(13, 'fr', 'Garmin Forerunner 255', 'GPS et cardiofréquencemètre intégré'),
(13, 'en', 'Garmin Forerunner 255', 'GPS and integrated heart rate monitor'),
(14, 'fr', 'Polar Vantage V2', 'Analyse avancée de la performance'),
(14, 'en', 'Polar Vantage V2', 'Advanced performance analysis'),
(15, 'fr', 'Apple Watch SE', 'Fitness et santé au quotidien'),
(15, 'en', 'Apple Watch SE', 'Daily fitness and health')
ON CONFLICT (product_id, locale) DO NOTHING;

-- PRODUCT SIZES
INSERT INTO product_sizes (product_id, taille, stock) VALUES
-- CHAUSSURES (40-44)
(1, '40', 10), (1, '41', 10), (1, '42', 10), (1, '43', 10), (1, '44', 10),
(2, '40', 10), (2, '41', 10), (2, '42', 10), (2, '43', 10), (2, '44', 10),
(3, '40', 10), (3, '41', 10), (3, '42', 10), (3, '43', 10), (3, '44', 10),
(4, '40', 10), (4, '41', 10), (4, '42', 10), (4, '43', 10), (4, '44', 10),
-- VETEMENTS (S, M, L, XL)
(5, 'S', 20), (5, 'M', 20), (5, 'L', 20), (5, 'XL', 20),
(6, 'S', 20), (6, 'M', 20), (6, 'L', 20), (6, 'XL', 20),
(7, 'S', 10), (7, 'M', 10), (7, 'L', 10), (7, 'XL', 10),
(8, 'S', 20), (8, 'M', 20), (8, 'L', 20), (8, 'XL', 20)
ON CONFLICT (product_id, taille) DO NOTHING;
