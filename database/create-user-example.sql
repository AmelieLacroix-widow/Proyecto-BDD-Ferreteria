-- Script de ejemplo: crear usuario del proyecto
-- Cada integrante reemplaza TU_CONTRASEÑA por su contraseña local

CREATE USER 'equipo'@'%' IDENTIFIED BY 'TU_CONTRASEÑA';

-- Dar permisos a la base de datos 'ferreteria'
GRANT ALL PRIVILEGES ON ferreteria.* TO 'equipo'@'%';

FLUSH PRIVILEGES;