package ru.shoichi.films.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnector {
    private static final Config config = ConfigFactory.load();
    private static final String JDBC_URL = config.getString("db.url");
    private static final String DRIVER_NAME = config.getString("db.driver");
    private static final String USERNAME = config.getString("db.username");
    private static final String PASSWORD = config.getString("db.password");
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        config.setDriverClassName(DRIVER_NAME);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setConnectionTimeout(50000);
        config.setMaximumPoolSize(100);
        config.setAutoCommit(false);
        dataSource = new HikariDataSource(config);
    }

    public Connection GetConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
