package com.sherry.converter;

import com.sherry.enums.Database;
import com.sherry.model.Column;
import com.sherry.model.Table;
import com.sherry.util.JDBC;

import java.util.List;

public interface DatabaseParser {

    Table table(JDBC jdbc, String databaseName, String tableName);

    List<Column> columns(JDBC jdbc, String databaseName, String tableName);

    List<String> enums();

    String getFileName(String columnName, String columnType);

    String dataTypeToJdbcType(String dataType);

    String jdbcTypeToJavaType(String jdbcType);

    String javaTypeToEnum(String tableName, String columnType, String javaType, String fieldName);

    static DatabaseParser getParser(Database database) {
        if (database == Database.MYSQL) {
            return new MySQLParser();
        }
        throw new RuntimeException("Database parser not found");
    }
}