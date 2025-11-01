package com.autosolutions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class AutoSolutionsApplicationTests {

  @DynamicPropertySource
  static void dbProps(DynamicPropertyRegistry r) {
    // ---- DataSource (Oracle local) ----
    r.add("spring.datasource.url", () -> "jdbc:oracle:thin:@localhost:1521/XEPDB1");
    r.add("spring.datasource.driver-class-name", () -> "oracle.jdbc.OracleDriver");
    r.add("spring.datasource.username", () -> "GERSON");
    r.add("spring.datasource.password", () -> "Catolica10");

    // ---- JPA/Hibernate ----
    r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    r.add("spring.jpa.open-in-view", () -> "false");
    r.add("spring.jpa.properties.hibernate.default_schema", () -> "GERSON");

    // ---- Flyway ----
    r.add("spring.flyway.enabled", () -> "true");
    r.add("spring.flyway.locations", () -> "classpath:db/migration");
    r.add("spring.flyway.table", () -> "FLYWAY_SCHEMA_HISTORY_AS");
    r.add("spring.flyway.default-schema", () -> "GERSON");
    r.add("spring.flyway.schemas", () -> "GERSON");
    r.add("spring.flyway.baseline-on-migrate", () -> "true");
  }

  @Test
  void contextLoads() { }
}
