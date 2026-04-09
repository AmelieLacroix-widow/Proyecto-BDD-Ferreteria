-- Script de ejemplo: crear usuario del proyecto
-- Cada integrante reemplaza TU_CONTRASEÑA por su contraseña local

CREATE USER 'equipo'@'%' IDENTIFIED BY '1234';

-- Dar permisos a la base de datos 'ferreteria'
GRANT ALL PRIVILEGES ON ferreteria_alanis.* TO 'equipo'@'%';

FLUSH PRIVILEGES;
