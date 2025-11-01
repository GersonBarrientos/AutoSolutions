-- Habilita creación de usuarios en XE (solo para pruebas)
ALTER SESSION SET "_ORACLE_SCRIPT"=true;

-- Crea el usuario/ESQUEMA que usas en dev
CREATE USER GERSON IDENTIFIED BY Catolica10;
GRANT CONNECT, RESOURCE TO GERSON;
ALTER USER GERSON QUOTA UNLIMITED ON USERS;
