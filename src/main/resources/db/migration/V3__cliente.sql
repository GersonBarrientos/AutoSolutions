--------------------------------------------------------------------------------
-- V3__cliente.sql (Oracle, idempotente)
-- Asegura: tabla CLIENTE, índices y secuencia CLIENTE_SEQ.
-- Importante: NO modifica si ya existen (evita errores en re-ejecución).
--------------------------------------------------------------------------------

-- 1) Tabla CLIENTE (sólo si no existe)
DECLARE v_cnt NUMBER; BEGIN
  SELECT COUNT(*) INTO v_cnt FROM user_tables WHERE table_name = 'CLIENTE';
  IF v_cnt = 0 THEN
    EXECUTE IMMEDIATE '
      CREATE TABLE GERSON.CLIENTE (
        ID         NUMBER(10)      NOT NULL,
        NOMBRE     VARCHAR2(120)   NOT NULL,
        TELEFONO   VARCHAR2(50),
        EMAIL      VARCHAR2(120),
        DIRECCION  VARCHAR2(255),
        CONSTRAINT PK_CLIENTE PRIMARY KEY (ID)
      )
    ';
  END IF;
END;
/
-- 2) Secuencia CLIENTE_SEQ (sólo si no existe)
DECLARE v_cnt NUMBER; BEGIN
  SELECT COUNT(*) INTO v_cnt FROM user_sequences WHERE sequence_name = 'CLIENTE_SEQ';
  IF v_cnt = 0 THEN
    EXECUTE IMMEDIATE 'CREATE SEQUENCE GERSON.CLIENTE_SEQ START WITH 1 INCREMENT BY 1 NOCACHE';
  END IF;
END;
/
-- 3) Índice por NOMBRE (sólo si no existe)
DECLARE v_cnt NUMBER; BEGIN
  SELECT COUNT(*) INTO v_cnt FROM user_indexes WHERE index_name = 'IX_CLIENTE_NOMBRE';
  IF v_cnt = 0 THEN
    EXECUTE IMMEDIATE 'CREATE INDEX GERSON.IX_CLIENTE_NOMBRE ON GERSON.CLIENTE (NOMBRE)';
  END IF;
END;
/
-- 4) Índice por TELEFONO (sólo si no existe)
DECLARE v_cnt NUMBER; BEGIN
  SELECT COUNT(*) INTO v_cnt FROM user_indexes WHERE index_name = 'IX_CLIENTE_TELEFONO';
  IF v_cnt = 0 THEN
    EXECUTE IMMEDIATE 'CREATE INDEX GERSON.IX_CLIENTE_TELEFONO ON GERSON.CLIENTE (TELEFONO)';
  END IF;
END;
/
