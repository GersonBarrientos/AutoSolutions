--------------------------------------------------------------------------------
-- V11__add_version_column_orden_trabajo.sql
-- Agrega columna VERSION en ORDEN_TRABAJO para @Version (bloqueo optimista).
-- Idempotente: no falla si la columna ya existe.
--------------------------------------------------------------------------------

DECLARE
  v_exists NUMBER;
BEGIN
  SELECT COUNT(*)
    INTO v_exists
    FROM user_tab_cols
   WHERE table_name = 'ORDEN_TRABAJO'
     AND column_name = 'VERSION';

  IF v_exists = 0 THEN
    EXECUTE IMMEDIATE '
      ALTER TABLE ORDEN_TRABAJO
      ADD (VERSION NUMBER(19) DEFAULT 0 NOT NULL)
    ';
  ELSE
    -- Si ya existe, asegurar valores y default/not null
    EXECUTE IMMEDIATE '
      UPDATE ORDEN_TRABAJO
         SET VERSION = NVL(VERSION, 0)
    ';
    EXECUTE IMMEDIATE '
      ALTER TABLE ORDEN_TRABAJO
      MODIFY (VERSION DEFAULT 0 NOT NULL)
    ';
  END IF;
END;
/
