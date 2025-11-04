--------------------------------------------------------------------------------
-- V12__rename_subtotals_en_orden_trabajo.sql
-- Alinea nombres de columnas con la entidad JPA:
--   SUBTOTAL_MO  -> SUBTOTAL_MANO_OBRA
--   SUBTOTAL_REP -> SUBTOTAL_REPUESTOS
--------------------------------------------------------------------------------

DECLARE
  v_has_subtotal_mo    NUMBER;
  v_has_subtotal_mo_ok NUMBER;
  v_has_subtotal_rep   NUMBER;
  v_has_subtotal_rep_ok NUMBER;
BEGIN
  -- Estados actuales
  SELECT COUNT(*) INTO v_has_subtotal_mo
    FROM user_tab_cols
   WHERE table_name = 'ORDEN_TRABAJO'
     AND column_name = 'SUBTOTAL_MO';

  SELECT COUNT(*) INTO v_has_subtotal_mo_ok
    FROM user_tab_cols
   WHERE table_name = 'ORDEN_TRABAJO'
     AND column_name = 'SUBTOTAL_MANO_OBRA';

  SELECT COUNT(*) INTO v_has_subtotal_rep
    FROM user_tab_cols
   WHERE table_name = 'ORDEN_TRABAJO'
     AND column_name = 'SUBTOTAL_REP';

  SELECT COUNT(*) INTO v_has_subtotal_rep_ok
    FROM user_tab_cols
   WHERE table_name = 'ORDEN_TRABAJO'
     AND column_name = 'SUBTOTAL_REPUESTOS';

  -- Renombrar SUBTOTAL_MO -> SUBTOTAL_MANO_OBRA
  IF v_has_subtotal_mo = 1 AND v_has_subtotal_mo_ok = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE ORDEN_TRABAJO RENAME COLUMN SUBTOTAL_MO TO SUBTOTAL_MANO_OBRA';
  END IF;

  -- Renombrar SUBTOTAL_REP -> SUBTOTAL_REPUESTOS
  IF v_has_subtotal_rep = 1 AND v_has_subtotal_rep_ok = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE ORDEN_TRABAJO RENAME COLUMN SUBTOTAL_REP TO SUBTOTAL_REPUESTOS';
  END IF;
END;
/
