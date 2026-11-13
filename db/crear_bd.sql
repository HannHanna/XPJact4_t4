-- Base de datos y usuario de la Actividad 4 (XPJact4_t4).
-- Cambia CONTRASENA_SEGURA por una contraseña propia antes de ejecutarlo.
-- Las tablas (usuario, categoria, producto) las crea Spring Boot automáticamente al arrancar.
CREATE DATABASE IF NOT EXISTS xpjact4_t4 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'xpj_act4'@'localhost' IDENTIFIED BY 'CONTRASENA_SEGURA';
GRANT ALL PRIVILEGES ON xpjact4_t4.* TO 'xpj_act4'@'localhost';
FLUSH PRIVILEGES;
