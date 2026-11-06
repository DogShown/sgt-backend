package com.sgt.sgt_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("sqlserver-test")
class SqlServerConnectionIT {
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void conectaAoSqlServerConfigurado() {
        Integer resultado=jdbcTemplate.queryForObject("SELECT 1",Integer.class);
        assertEquals(1,resultado);
    }
}