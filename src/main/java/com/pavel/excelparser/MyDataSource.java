package com.pavel.excelparser;

import com.zaxxer.hikari.HikariConfig;

public class MyDataSource {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    MyDataSource(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public javax.sql.DataSource buildDatasource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( "jdbc:postgresql://localhost:5432/test_task" );
        config.setUsername( "chistyakov" );
        config.setPassword( "1234" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        return new com.zaxxer.hikari.HikariDataSource(config);
    }
}
