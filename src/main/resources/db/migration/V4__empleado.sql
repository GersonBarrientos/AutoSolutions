-- V4__empleado.sql
-- Ajustes para EMPLEADO: agregar ROL_ID + FK a ROL(ID) si no existen

-- Nota: usar PL/SQL para que sea idempotente y no falle si ya existe
DECLARE
  v_cnt NUMBER;
BEGIN
  -- 1) Columna ROL_ID
  SELECT COUNT(*) INTO v_cnt
  FROM user_tab_cols
  WHERE table_name = 'EMPLEADO' AND column_name = 'ROL_ID';

  IF v_cnt = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE GERSON.EMPLEADO ADD (ROL_ID NUMBER(10))';
  END IF;

  -- 2) Índice para la FK
  SELECT COUNT(*) INTO v_cnt
  FROM user_indexes
  WHERE index_name = 'IX_EMPLEADO_ROL' AND table_name = 'EMPLEADO';

  IF v_cnt = 0 THEN
    EXECUTE IMMEDIATE 'CREATE INDEX GERSON.IX_EMPLEADO_ROL ON GERSON.EMPLEADO(ROL_ID)';
  END IF;

  -- 3) FK a ROL(ID)
  SELECT COUNT(*) INTO v_cnt
  FROM user_constraints
  WHERE table_name = 'EMPLEADO' AND constraint_name = 'FK_EMPLEADO_ROL';

  IF v_cnt = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE GERSON.EMPLEADO
                       ADD CONSTRAINT FK_EMPLEADO_ROL
                       FOREIGN KEY (ROL_ID)
                       REFERENCES GERSON.ROL(ID)';
  END IF;
END;
/
