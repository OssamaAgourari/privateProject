-- Create and use database
CREATE DATABASE IF NOT EXISTS employee_management;
USE employee_management;

-- Drop existing tables if they exist (in correct order)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS pointage;
DROP TABLE IF EXISTS salaire;
DROP TABLE IF EXISTS utilisateur;
DROP TABLE IF EXISTS employe;
DROP TABLE IF EXISTS service;
SET FOREIGN_KEY_CHECKS = 1;

-- Create tables
CREATE TABLE service (
    idService INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE employe (
    idEmploye INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    poste VARCHAR(100) NOT NULL,
    idService INT,
    dateEmbauche DATE NOT NULL,
    salaireDeBase DECIMAL(10,2) NOT NULL,
    actif BOOLEAN DEFAULT true,
    cin VARCHAR(255),
    cnss VARCHAR(255),
    telephone VARCHAR(255),
    email VARCHAR(255),
    adresse VARCHAR(255),
    statut VARCHAR(255),
    typeDeContrat VARCHAR(255),
    FOREIGN KEY (idService) REFERENCES service(idService)
);

CREATE TABLE utilisateur (
    idUtilisateur INT PRIMARY KEY AUTO_INCREMENT,
    nomUtilisateur VARCHAR(50) NOT NULL UNIQUE,
    motDePasse VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    idEmploye INT,
    FOREIGN KEY (idEmploye) REFERENCES employe(idEmploye)
);

CREATE TABLE salaire (
    idSalaire INT PRIMARY KEY AUTO_INCREMENT,
    idEmploye INT NOT NULL,
    mois INT NOT NULL,
    annee INT NOT NULL,
    salaireBrut DECIMAL(10,2) NOT NULL,
    deductions DECIMAL(10,2) NOT NULL,
    salaireNet DECIMAL(10,2) NOT NULL,
    datePaiement DATE NOT NULL,
    FOREIGN KEY (idEmploye) REFERENCES employe(idEmploye)
);

CREATE TABLE pointage (
    idPointage INT PRIMARY KEY AUTO_INCREMENT,
    idEmploye INT NOT NULL,
    date DATE NOT NULL,
    heureArrivee TIME NOT NULL,
    heureDepart TIME,
    FOREIGN KEY (idEmploye) REFERENCES employe(idEmploye)
);

-- Insert initial data
INSERT INTO service (nom, description) 
VALUES ('Administration', 'Service administratif principal');

INSERT INTO employe (nom, prenom, poste, idService, dateEmbauche, salaireDeBase, actif)
VALUES ('Admin', 'System', 'Administrateur', 1, CURDATE(), 5000.00, true);

INSERT INTO utilisateur (nomUtilisateur, motDePasse, role, idEmploye)
VALUES ('admin', '$2a$12$.ggl1.xvc13mKuncwl6..uyRGpvkiKL2VnHBMx3bzgkCki1FiDdH6', 'ADMIN', 1); 