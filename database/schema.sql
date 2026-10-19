-- =========================================================
-- TRAVEL & TOURISM MANAGEMENT SYSTEM
-- FINAL DATABASE SCHEMA - FRESH INSTALLATION
-- =========================================================
-- IMPORTANT:
-- This script is for a NEW/FRESH database setup.
-- It DROPS existing project tables and sample data.
-- DO NOT run it on the currently working database unless
-- you intentionally want to rebuild the database from scratch.
-- =========================================================

CREATE DATABASE IF NOT EXISTS travel_tourism;
USE travel_tourism;

-- Drop in dependency order so the script can be rerun cleanly.
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS transport;
DROP TABLE IF EXISTS hotels;
DROP TABLE IF EXISTS travel_packages;
DROP TABLE IF EXISTS destinations;
DROP TABLE IF EXISTS users;

-- =========================================================
-- 1. USERS
-- =========================================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER'
);

-- =========================================================
-- 2. DESTINATIONS
-- =========================================================
CREATE TABLE destinations (
    destination_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    famous_for VARCHAR(255)
);

-- =========================================================
-- 3. TRAVEL PACKAGES
-- =========================================================
CREATE TABLE travel_packages (
    package_id INT PRIMARY KEY AUTO_INCREMENT,
    package_name VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    duration INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    available_seats INT NOT NULL
);

-- =========================================================
-- 4. HOTELS
-- =========================================================
CREATE TABLE hotels (
    hotel_id INT PRIMARY KEY AUTO_INCREMENT,
    hotel_name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    available_rooms INT NOT NULL,
    rating DECIMAL(2,1)
);

-- =========================================================
-- 5. TRANSPORT
-- =========================================================
CREATE TABLE transport (
    transport_id INT PRIMARY KEY AUTO_INCREMENT,
    transport_type ENUM('BUS', 'TRAIN', 'FLIGHT') NOT NULL,
    operator_name VARCHAR(100) NOT NULL,
    transport_number VARCHAR(50) NOT NULL,
    departure VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    distance_km DECIMAL(10,2) NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    fare_per_km DECIMAL(10,2) DEFAULT NULL,
    ticket_price DECIMAL(10,2) DEFAULT NULL,
    available_seats INT NOT NULL,
    UNIQUE (transport_type, transport_number, departure, destination)
);

-- =========================================================
-- 6. BOOKINGS
-- =========================================================
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    package_id INT NOT NULL,
    hotel_id INT NULL,
    transport_id INT NULL,
    booking_date DATE NOT NULL,
    travel_date DATE NOT NULL,
    number_of_people INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',

    CONSTRAINT fk_booking_customer
        FOREIGN KEY (customer_id) REFERENCES users(user_id),

    CONSTRAINT fk_booking_package
        FOREIGN KEY (package_id) REFERENCES travel_packages(package_id),

    CONSTRAINT fk_booking_hotel
        FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id),

    CONSTRAINT fk_booking_transport
        FOREIGN KEY (transport_id) REFERENCES transport(transport_id)
);

-- =========================================================
-- 7. PAYMENTS
-- =========================================================
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('UPI', 'CARD', 'NET_BANKING', 'CASH') NOT NULL,
    payment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_status ENUM('PENDING', 'SUCCESS', 'FAILED') NOT NULL DEFAULT 'PENDING',

    CONSTRAINT fk_payment_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

-- =========================================================
-- SAMPLE USERS
-- =========================================================
INSERT INTO users
(name, email, phone, username, password, role)
VALUES
('Administrator', 'admin@travel.com', '9999999999', 'admin', 'admin123', 'ADMIN'),
('Shivam Kumar', 'customer@travel.com', '9876543210', 'customer', '1234', 'CUSTOMER');

-- =========================================================
-- SAMPLE DESTINATIONS
-- =========================================================
INSERT INTO destinations
(name, location, description, famous_for)
VALUES
('Manali', 'Himachal Pradesh', 'Mountains, snow and adventure', 'Snow, mountains and adventure'),
('Goa', 'Goa', 'Beaches and nightlife', 'Beaches and nightlife'),
('Jaipur', 'Rajasthan', 'Forts and palaces', 'Forts and palaces');

-- =========================================================
-- SAMPLE TRAVEL PACKAGES
-- =========================================================
INSERT INTO travel_packages
(package_name, destination, duration, price, available_seats)
VALUES
('Manali Adventure', 'Manali', 5, 15000.00, 20),
('Goa Beach Trip', 'Goa', 4, 12000.00, 25),
('Jaipur Heritage', 'Jaipur', 3, 9000.00, 30);

-- =========================================================
-- SAMPLE HOTELS
-- =========================================================
INSERT INTO hotels
(hotel_name, location, price_per_night, available_rooms, rating)
VALUES
('Mountain View Hotel', 'Manali', 2500.00, 15, 4.5),
('Sea View Resort', 'Goa', 3500.00, 10, 4.3),
('Royal Palace Hotel', 'Jaipur', 2200.00, 20, 4.2);

-- =========================================================
-- SAMPLE BUS ROUTES
-- =========================================================
INSERT INTO transport
(transport_type, operator_name, transport_number, departure, destination,
 distance_km, departure_time, arrival_time, fare_per_km, ticket_price, available_seats)
VALUES
('BUS', 'HRTC', 'HR01-1001', 'Delhi', 'Manali', 550.00, '08:00:00', '18:00:00', 7.00, NULL, 40),
('BUS', 'RSRTC', 'RJ14-2001', 'Delhi', 'Jaipur', 280.00, '07:00:00', '12:00:00', 7.00, NULL, 45),
('BUS', 'UPSRTC', 'UP16-3001', 'Delhi', 'Agra', 230.00, '09:00:00', '13:00:00', 6.50, NULL, 50);

-- =========================================================
-- SAMPLE TRAIN ROUTES
-- =========================================================
INSERT INTO transport
(transport_type, operator_name, transport_number, departure, destination,
 distance_km, departure_time, arrival_time, fare_per_km, ticket_price, available_seats)
VALUES
('TRAIN', 'Indian Railways', '12001', 'New Delhi', 'Bhopal', 700.00, '06:00:00', '14:00:00', NULL, 1200.00, 100),
('TRAIN', 'Indian Railways', '12952', 'New Delhi', 'Mumbai', 1380.00, '16:55:00', '08:35:00', NULL, 1800.00, 120),
('TRAIN', 'Indian Railways', '12430', 'New Delhi', 'Lucknow', 512.00, '20:50:00', '06:45:00', NULL, 1100.00, 100),
('TRAIN', 'Indian Railways', '12049', 'New Delhi', 'Chandigarh', 244.00, '06:15:00', '09:25:00', NULL, 800.00, 80);

-- =========================================================
-- SAMPLE FLIGHT ROUTES
-- =========================================================
INSERT INTO transport
(transport_type, operator_name, transport_number, departure, destination,
 distance_km, departure_time, arrival_time, fare_per_km, ticket_price, available_seats)
VALUES
('FLIGHT', 'Air India', 'AI-101', 'Delhi', 'Mumbai', 1150.00, '08:00:00', '10:15:00', NULL, 6500.00, 150),
('FLIGHT', 'IndiGo', '6E-201', 'Delhi', 'Bangalore', 1740.00, '09:30:00', '12:20:00', NULL, 7500.00, 180),
('FLIGHT', 'Air India Express', 'IX-301', 'Delhi', 'Goa', 1500.00, '11:00:00', '13:40:00', NULL, 6000.00, 160),
('FLIGHT', 'IndiGo', '6E-401', 'Delhi', 'Kolkata', 1300.00, '14:00:00', '16:20:00', NULL, 7000.00, 170);

-- =========================================================
-- END OF FINAL FRESH-INSTALLATION SCHEMA
-- =========================================================
