-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS employee_management;
USE employee_management;

-- Drop tables in correct order due to foreign key constraints
DROP TABLE IF EXISTS pointage;
DROP TABLE IF EXISTS salaire;
DROP TABLE IF EXISTS utilisateur;
DROP TABLE IF EXISTS employe;
DROP TABLE IF EXISTS service;

-- Create service table
CREATE TABLE IF NOT EXISTS service (
    id_service INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    description TEXT
);

-- Create employe table
CREATE TABLE IF NOT EXISTS employe (
    id_employe INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    poste VARCHAR(100) NOT NULL,
    id_service INT,
    date_embauche DATE NOT NULL,
    salaire_de_base DECIMAL(10,2) NOT NULL,
    actif BOOLEAN DEFAULT true,
    cin VARCHAR(255),
    cnss VARCHAR(255),
    telephone VARCHAR(255),
    email VARCHAR(255),
    adresse VARCHAR(255),
    statut VARCHAR(255),
    type_de_contrat VARCHAR(255),
    FOREIGN KEY (id_service) REFERENCES service(id_service)
);

-- Create utilisateur table
CREATE TABLE IF NOT EXISTS utilisateur (
    id_utilisateur INT PRIMARY KEY AUTO_INCREMENT,
    nom_utilisateur VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    id_employe INT,
    FOREIGN KEY (id_employe) REFERENCES employe(id_employe)
);

-- Create salaire table
CREATE TABLE IF NOT EXISTS salaire (
    id_salaire INT PRIMARY KEY AUTO_INCREMENT,
    id_employe INT NOT NULL,
    mois INT NOT NULL,
    annee INT NOT NULL,
    salaire_brut DECIMAL(10,2) NOT NULL,
    deductions DECIMAL(10,2) NOT NULL,
    salaire_net DECIMAL(10,2) NOT NULL,
    date_paiement DATE NOT NULL,
    FOREIGN KEY (id_employe) REFERENCES employe(id_employe)
);

-- Create pointage table
CREATE TABLE IF NOT EXISTS pointage (
    id_pointage INT PRIMARY KEY AUTO_INCREMENT,
    id_employe INT NOT NULL,
    date DATE NOT NULL,
    heure_arrivee TIME NOT NULL,
    heure_depart TIME,
    FOREIGN KEY (id_employe) REFERENCES employe(id_employe)
); 