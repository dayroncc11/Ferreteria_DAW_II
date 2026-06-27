CREATE DATABASE IF NOT EXISTS ferreteria;
USE ferreteria;

-- ══════════════════════════════════════════════════════════════
--  Tablas
-- ══════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    clave VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    correo VARCHAR(50) NOT NULL UNIQUE,
    foto LONGTEXT
);

CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dni_ruc VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    telefono VARCHAR(100) NOT NULL,
    direccion VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS proveedor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ruc VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    proveedor_id BIGINT,
    FOREIGN KEY (proveedor_id) REFERENCES proveedor(id)
);

CREATE TABLE IF NOT EXISTS venta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2),
    cliente_id BIGINT,
    usuario_id BIGINT,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS detalle_venta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT,
    producto_id BIGINT,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2),
    subtotal DECIMAL(10,2),
    FOREIGN KEY (venta_id) REFERENCES venta(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

CREATE TABLE IF NOT EXISTS devolucion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(255),
    venta_id BIGINT,
    FOREIGN KEY (venta_id) REFERENCES venta(id)
);

-- ══════════════════════════════════════════════════════════════
--  Datos iniciales (INSERT IGNORE = no falla si ya existen)
-- ══════════════════════════════════════════════════════════════

INSERT IGNORE INTO usuario (id, username, clave, rol, nombre, correo) VALUES
(1, 'admin', '$2a$10$nS1ZoavMNzmAdO8xXav1vuFDsnQCE6NPVVpYKudqI7EHrSAGGYA12', 'admin', 'Administrador', 'admin@ferreteria.com'),
(2, 'empleado1', '$2a$10$QO6T4HgzqLqubLGhuelfeumrA6ZNl6tnHF4fmi4QbtO5W3yWeLASG', 'empleado','Carlos Mendoza', 'carlos@ferreteria.com'),
(3, 'admin1', '$2a$10$7himfwhQ98AIqGcQ3nKEMOSTiyK3wwtPNivmz8dnOr0cOs3m7TCum', 'admin', 'Dayron Jesus', 'daycc11@ferreteria.com');

INSERT IGNORE INTO proveedor (id, nombre, ruc, telefono) VALUES
(1, 'Distribuidora Aceros del Sur S.A.C.', '20512345678', '01-4567890'),
(2, 'Pinturas Nacional S.A.',              '20198765432', '01-3456789'),
(3, 'Herramientas Pro E.I.R.L.',           '20345678901', '01-2345678'),
(4, 'Electricidad Total S.A.C.',           '20456789012', '01-5678901'),
(5, 'Cementos y Materiales Lima S.A.',     '20567890123', '01-6789012');

INSERT IGNORE INTO producto (id, nombre, precio, stock, categoria, proveedor_id) VALUES
(1,  'Martillo de Carpintero 16oz',     35.90,  50,  'Herramientas',            3),
(2,  'Destornillador Phillips #2',      12.50,  80,  'Herramientas',            3),
(3,  'Taladro Percutor 13mm 750W',     189.90,  15,  'Herramientas Eléctricas', 3),
(4,  'Pintura Látex Blanca 4L',         62.00,  30,  'Pinturas',                2),
(5,  'Pintura Esmalte Azul 1L',         28.50,  25,  'Pinturas',                2),
(6,  'Cable THW 14 AWG (rollo 100m)',  145.00,  20,  'Electricidad',            4),
(7,  'Tomacorriente Doble con Tierra',   8.90, 100,  'Electricidad',            4),
(8,  'Interruptor Simple',               6.50, 120,  'Electricidad',            4),
(9,  'Cemento Portland Tipo I (42.5kg)', 28.50, 200,  'Materiales',             5),
(10, 'Varilla de Acero 1/2" x 9m',     32.00, 150,  'Materiales',              1),
(11, 'Llave Inglesa Ajustable 10"',     42.00,  35,  'Herramientas',            3),
(12, 'Sierra Circular 7-1/4" 1400W',   259.90,   8,  'Herramientas Eléctricas', 3),
(13, 'Cinta Métrica 5m',                15.90,  60,  'Herramientas',            3),
(14, 'Tubo PVC 4" x 3m',               22.00,  45,  'Materiales',              5),
(15, 'Disco de Corte Metal 7"',          7.50,   3,  'Herramientas',            3);

INSERT IGNORE INTO cliente (id, dni_ruc, nombre, activo, telefono, direccion) VALUES
(1, '45678901', 'Juan Carlos Pérez López',    TRUE, '987654321', 'Av. Los Olivos 123, Lima'),
(2, '32165498', 'María Elena Torres Ríos',    TRUE, '912345678', 'Jr. Las Flores 456, SMP'),
(3, '78901234', 'Roberto Sánchez Vargas',     TRUE, '956789012', 'Calle El Sol 789, Comas'),
(4, '65432109', 'Ana Lucía Mendoza Cruz',     TRUE, '934567890', 'Av. Perú 321, Los Olivos'),
(5, '12345678', 'Luis Alberto Ramírez Díaz',  TRUE, '978901234', 'Jr. Junín 654, Independencia');

INSERT IGNORE INTO venta(id, fecha, total, cliente_id, usuario_id) VALUES
(1, '2026-06-15 09:15:00', 71.80, 1, 2),
(2, '2026-06-15 10:30:00', 62.00, 2, 2),
(3, '2026-06-15 11:45:00', 145.00, 3, 2),
(4, '2026-06-15 14:20:00', 57.00, 4, 2),
(5, '2026-06-15 16:10:00', 189.90, 5, 2);

INSERT IGNORE INTO detalle_venta (id, venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 1, 2, 35.90, 71.80),
(2, 2, 4, 1, 62.00, 62.00),
(3, 3, 6, 1, 145.00, 145.00),
(4, 4, 5, 2, 28.50, 57.00),
(5, 5, 3, 1, 189.90, 189.90);
