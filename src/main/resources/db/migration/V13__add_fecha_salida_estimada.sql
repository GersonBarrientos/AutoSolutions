DECLARE
  v_missing NUMBER;
  v_has_fs  NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_missing
  FROM user_tab_cols
  WHERE table_name = 'ORDEN_TRABAJO'
    AND column_name = 'FECHA_SALIDA_ESTIMADA';

  IF v_missing = 0 THEN
    EXECUTE IMMEDIATE '
      ALTER TABLE ORDEN_TRABAJO
      ADD (FECHA_SALIDA_ESTIMADA TIMESTAMP(6) NULL)
    ';

    -- Si existe FECHA_SALIDA, inicializamos
    SELECT COUNT(*) INTO v_has_fs
    FROM user_tab_cols
    WHERE table_name = 'ORDEN_TRABAJO'
      AND column_name = 'FECHA_SALIDA';
    IF v_has_fs = 1 THEN
      EXECUTE IMMEDIATE '
        UPDATE ORDEN_TRABAJO
           SET FECHA_SALIDA_ESTIMADA = FECHA_SALIDA
        WHERE FECHA_SALIDA_ESTIMADA IS NULL
      ';
    END IF;
  END IF;
END;
/
