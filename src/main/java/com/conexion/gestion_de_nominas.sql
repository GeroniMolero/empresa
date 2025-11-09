-- ==========================================================
--  BASE DE DATOS: gestion_de_nominas
--  Fiel al modelo Java (Persona, Empleado, Nomina)
-- ==========================================================

CREATE DATABASE IF NOT EXISTS gestion_de_nominas
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE gestion_de_nominas;

-- ==========================================================
--  TABLA EMPLEADOS
-- ==========================================================
DROP TABLE IF EXISTS empleados;

CREATE TABLE empleados (
  dni VARCHAR(9) NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  sexo CHAR(1) NOT NULL CHECK (sexo IN ('M','F')),
  categoria INT NOT NULL CHECK (categoria BETWEEN 1 AND 10),
  anyos INT NOT NULL CHECK (anyos >= 0),
  PRIMARY KEY (dni)
);

-- ==========================================================
--  TABLA NOMINAS
-- ==========================================================
DROP TABLE IF EXISTS nominas;

CREATE TABLE nominas (
  dni VARCHAR(9) NOT NULL,
  sueldo DOUBLE NOT NULL,
  PRIMARY KEY (dni),
  FOREIGN KEY (dni) REFERENCES empleados(dni)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- ==========================================================
--  DATOS DE PRUEBA (empleados)
-- ==========================================================
INSERT INTO empleados (dni, nombre, sexo, categoria, anyos) VALUES
('11111111A', 'Juan Pérez', 'M', 4, 8),
('22222222B', 'María Gómez', 'F', 6, 10),
('33333333C', 'Carlos López', 'M', 3, 4),
('44444444D', 'Ana Fernández', 'F', 9, 15),
('55555555E', 'Luis Ramírez', 'M', 2, 2);

-- ==========================================================
--  NOMINAS (calculadas según Nomina.java)
-- ==========================================================
-- Sueldo = SUELDO_BASE[categoria - 1] + (5000 * anyos)
-- SUELDO_BASE = {50000, 70000, 90000, 110000, 130000, 150000, 170000, 190000, 210000, 230000}

INSERT INTO nominas (dni, sueldo) VALUES
('11111111A', 110000 + (8 * 5000)),   -- categoría 4
('22222222B', 150000 + (10 * 5000)),  -- categoría 6
('33333333C', 90000 + (4 * 5000)),    -- categoría 3
('44444444D', 210000 + (15 * 5000)),  -- categoría 9
('55555555E', 70000 + (2 * 5000));    -- categoría 2

-- ==========================================================
--  TRIGGER: recalcular sueldo al actualizar empleado
-- ==========================================================
DELIMITER //

CREATE TRIGGER recalcular_sueldo
AFTER UPDATE ON empleados
FOR EACH ROW
BEGIN
    DECLARE base INT;
    DECLARE nuevo_sueldo DOUBLE;
    
    CASE NEW.categoria
        WHEN 1 THEN SET base = 50000;
        WHEN 2 THEN SET base = 70000;
        WHEN 3 THEN SET base = 90000;
        WHEN 4 THEN SET base = 110000;
        WHEN 5 THEN SET base = 130000;
        WHEN 6 THEN SET base = 150000;
        WHEN 7 THEN SET base = 170000;
        WHEN 8 THEN SET base = 190000;
        WHEN 9 THEN SET base = 210000;
        WHEN 10 THEN SET base = 230000;
    END CASE;
    
    SET nuevo_sueldo = base + (NEW.anyos * 5000);
    UPDATE nominas SET sueldo = nuevo_sueldo WHERE dni = NEW.dni;
END //

DELIMITER ;

-- ==========================================================
--  CONSULTA DE VERIFICACIÓN
-- ==========================================================
SELECT * FROM empleados;
SELECT * FROM nominas;
