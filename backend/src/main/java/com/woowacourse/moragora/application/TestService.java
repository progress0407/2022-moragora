package com.woowacourse.moragora.application;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    private final JdbcTemplate dao;

    public TestService(final DataSource dataSource) {
        this.dao = new JdbcTemplate(dataSource);
    }

    public Integer query() {
        final String sql = "select count(1) from a where a.col > 1000 and a.col < 5000";
        final Integer count = dao.queryForObject(sql, Integer.class);
        return count;
    }

    public void command() {
        for (int i = 0; i < 30; i++) {
            dao.update("insert c values (7, 700)");
            dao.update("update c set col = 900 where id = 7");
            dao.update("delete from c where id = 7");
        }
    }
}
