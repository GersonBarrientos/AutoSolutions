-- V5__fix_tipo_columna_detalle_orden.sql
-- Ajusta el tipo de la columna TIPO para que coincida con la entidad (Hibernate espera VARCHAR2(20 CHAR))
ALTER TABLE DETALLE_ORDEN MODIFY (TIPO VARCHAR2(20 CHAR));
