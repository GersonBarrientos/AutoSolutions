-- V14__idx_orden_trabajo_fechas.sql
DECLARE v_missing NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_missing
  FROM user_indexes
  WHERE table_name = 'ORDEN_TRABAJO'
    AND index_name = 'IDX_OT_FECHAS';
  IF v_missing = 0 THEN
    EXECUTE IMMEDIATE '
      CREATE INDEX IDX_OT_FECHAS
        ON ORDEN_TRABAJO (FECHA_INGRESO, FECHA_SALIDA_ESTIMADA)
    ';
  END IF;
END;
/
