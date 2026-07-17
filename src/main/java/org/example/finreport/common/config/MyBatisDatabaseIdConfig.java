package org.example.finreport.common.config;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class MyBatisDatabaseIdConfig {

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        return new DatabaseIdProvider() {
            @Override
            public String getDatabaseId(DataSource dataSource) throws SQLException {
                try (Connection conn = dataSource.getConnection()) {
                    String productName = conn.getMetaData().getDatabaseProductName();
                    if (productName == null) return null;
                    productName = productName.toLowerCase();
                    if (productName.contains("mysql")) return "mysql";
                    if (productName.contains("h2")) return "h2";
                    return null;
                }
            }
        };
    }
}
