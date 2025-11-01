-- V6__add_auditing_columns_all.sql
-- Agrega CREATED_AT y UPDATED_AT en todas las tablas de negocio.
-- Compatible con nombres de tablas entrecomillados/minúsculas.

DECLARE
  PROCEDURE add_col_if_missing(p_table VARCHAR2, p_col VARCHAR2, p_col_def VARCHAR2) IS
    v_cnt NUMBER;
    v_sql VARCHAR2(4000);
  BEGIN
    SELECT COUNT(*)
      INTO v_cnt
      FROM user_tab_cols
     WHERE table_name = p_table  -- usa el valor EXACTO tal como viene de USER_TABLES
       AND column_name = UPPER(p_col);

    IF v_cnt = 0 THEN
      -- Citar el nombre de la tabla para respetar mayúsculas/minúsculas
      v_sql := 'ALTER TABLE GERSON."' || p_table || '" ADD (' || p_col_def || ')';
      EXECUTE IMMEDIATE v_sql;
    END IF;
  END;
BEGIN
  FOR t IN (
    SELECT table_name
      FROM user_tables
     WHERE UPPER(table_name) NOT LIKE '%FLYWAY%'
  ) LOOP
    -- CREATED_AT con default y NOT NULL
    add_col_if_missing(t.table_name, 'CREATED_AT', 'CREATED_AT TIMESTAMP(6) DEFAULT SYSTIMESTAMP NOT NULL');
    -- UPDATED_AT nullable
    add_col_if_missing(t.table_name, 'UPDATED_AT',  'UPDATED_AT  TIMESTAMP(6)');
  END LOOP;
END;
/
