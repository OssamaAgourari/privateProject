-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS employee_management;
USE employee_management;

-- Create default service
INSERT INTO service (nom, description) 
VALUES ('Administration', 'Service administratif principal')
ON DUPLICATE KEY UPDATE description = 'Service administratif principal';

-- Create default employee
INSERT INTO employe (nom, prenom, poste, id_service, date_embauche, salaire_de_base, actif)
SELECT 'Admin', 'System', 'Administrateur', id_service, CURDATE(), 5000.00, true
FROM service WHERE nom = 'Administration'
ON DUPLICATE KEY UPDATE actif = true;

-- Create admin user (if not exists)
INSERT INTO utilisateur (nom_utilisateur, mot_de_passe, role, id_employe)
SELECT 'admin', '$2a$12$.ggl1.xvc13mKuncwl6..uyRGpvkiKL2VnHBMx3bzgkCki1FiDdH6', 'ADMIN', id_employe
FROM employe WHERE nom = 'Admin' AND prenom = 'System'
ON DUPLICATE KEY UPDATE role = 'ADMIN'; 