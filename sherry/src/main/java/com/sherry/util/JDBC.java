package com.sherry.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBC implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(JDBC.class);

    private final Connection connection;

    private JDBC(Builder builder) throws Exception {
        Class.forName(builder.driver);
        connection = DriverManager.getConnection(builder.url, builder.username, builder.password);
    }

    public List<Map<String, Object>> query(String sql) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                dataList.add(row);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return dataList;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static class Builder {

        private String url;
        private String driver;
        private String username;
        private String password;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder driver(String driver) {
            this.driver = driver;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public JDBC build() throws Exception {
            return new JDBC(this);
        }
    }
}
