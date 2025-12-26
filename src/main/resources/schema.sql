-- Florist Management System Database Schema
-- Database: florist_db
-- DBMS: MySQL 8.0+

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS florist_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE florist_db;

-- Table: suppliers
-- Stores supplier/vendor information
CREATE TABLE IF NOT EXISTS suppliers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_supplier_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: flowers
-- Stores flower inventory with freshness tracking
CREATE TABLE IF NOT EXISTS flowers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(50),
    category VARCHAR(50),
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    arrival_date DATE NOT NULL,
    freshness_days INT NOT NULL COMMENT 'Number of days flower stays fresh',
    supplier_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL,
    INDEX idx_flower_name (name),
    INDEX idx_flower_category (category),
    INDEX idx_arrival_date (arrival_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: sales
-- Records all sales transactions
CREATE TABLE IF NOT EXISTS sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date DATE NOT NULL,
    flower_id INT NOT NULL,
    quantity_sold INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    customer_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flower_id) REFERENCES flowers(id) ON DELETE CASCADE,
    INDEX idx_sale_date (sale_date),
    INDEX idx_flower_id (flower_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: stock_alerts
-- Automated alerts for low stock and expiration warnings
CREATE TABLE IF NOT EXISTS stock_alerts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flower_id INT NOT NULL,
    alert_type VARCHAR(20) NOT NULL COMMENT 'LOW_STOCK or EXPIRY',
    severity VARCHAR(10) DEFAULT 'WARNING' COMMENT 'DANGER or WARNING',
    message TEXT NOT NULL,
    generated_date DATE NOT NULL,
    resolved BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flower_id) REFERENCES flowers(id) ON DELETE CASCADE,
    INDEX idx_alert_type (alert_type),
    INDEX idx_resolved (resolved),
    INDEX idx_flower_alert (flower_id, alert_type, resolved)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data for testing

-- Sample suppliers
-- INSERT INTO suppliers (name, phone, email) VALUES
-- ('Fleurs de Paris', '01-23-45-67-89', 'contact@fleursdeparts.fr'),
-- ('Garden Wholesale', '01-98-76-54-32', 'sales@gardenwholesale.fr'),
-- ('Tropical Imports', '01-55-44-33-22', 'info@tropicalimports.fr')
-- ON DUPLICATE KEY UPDATE name=name;

-- Sample flowers
-- INSERT INTO flowers (name, color, category, price, quantity, arrival_date, freshness_days, supplier_id) VALUES
-- ('Rose Rouge', 'Rouge', 'Rose', 3.50, 100, CURDATE(), 7, 1),
-- ('Tulipe Jaune', 'Jaune', 'Tulipe', 2.80, 75, CURDATE(), 5, 1),
-- ('Orchidée Blanche', 'Blanc', 'Orchidée', 15.00, 25, CURDATE(), 14, 3),
-- ('Lys Rose', 'Rose', 'Lys', 6.50, 50, CURDATE(), 10, 2),
-- ('Marguerite', 'Blanc', 'Marguerite', 1.50, 120, CURDATE(), 6, 2),
-- ('Tournesol', 'Jaune', 'Tournesol', 4.00, 60, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 7, 1)
-- ON DUPLICATE KEY UPDATE name=name;
