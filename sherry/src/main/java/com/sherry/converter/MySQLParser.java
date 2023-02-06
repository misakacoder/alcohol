package com.sherry.converter;

import com.sherry.model.Column;
import com.sherry.model.Table;
import com.sherry.util.JDBC;
import com.sherry.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MySQLParser implements DatabaseParser {

    private static final String COLUMN_INFO_SQL = "SELECT COLUMN_NAME, COLUMN_KEY, EXTRA, DATA_TYPE, COLUMN_TYPE, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s' ORDER BY ORDINAL_POSITION";

    private static final String TABLE_INFO_SQL = "SELECT TABLE_NAME, TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s'";

    private final List<String> enumList = new ArrayList<>();

    @Override
    public Table table(JDBC jdbc, String databaseName, String tableName) {
        List<Map<String, Object>> dataList = jdbc.query(String.format(TABLE_INFO_SQL, databaseName, tableName));
        for (Map<String, Object> data : dataList) {
            tableName = data.get("TABLE_NAME").toString();
            String tableComment = data.get("TABLE_COMMENT").toString();
            Table table = new Table();
            table.setName(tableName);
            table.setNeedAnnotation(!tableName.equals(tableName.toLowerCase()));
            table.setComment(tableComment);
            return table;
        }
        throw new RuntimeException("Table does not exist");
    }

    @Override
    public List<Column> columns(JDBC jdbc, String databaseName, String tableName) {
        enumList.clear();
        List<Map<String, Object>> dataList = jdbc.query(String.format(COLUMN_INFO_SQL, databaseName, tableName));
        List<Column> columnList = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            String columnName = data.get("COLUMN_NAME").toString();
            String columnKey = data.get("COLUMN_KEY").toString();
            String extra = data.get("EXTRA").toString();
            String jdbcType = dataTypeToJdbcType(data.get("DATA_TYPE").toString());
            String columnType = data.get("COLUMN_TYPE").toString();
            String columnComment = data.get("COLUMN_COMMENT").toString();
            String fieldName = getFileName(columnName, columnType);
            String javaType = javaTypeToEnum(tableName, columnType, jdbcTypeToJavaType(jdbcType), fieldName);
            Column column = new Column();
            column.setName(columnName);
            column.setNeedAnnotation(!columnName.equals(columnName.toLowerCase()));
            column.setType(columnType);
            column.setKey("PRI".equalsIgnoreCase(columnKey));
            column.setIncr("auto_increment".equalsIgnoreCase(extra));
            column.setFieldName(fieldName);
            column.setUpperFieldName(StringUtil.toUpperCaseFirstOne(fieldName));
            column.setJdbcType(jdbcType);
            column.setJavaType(javaType);
            column.setEnumType(!javaType.equals(jdbcTypeToJavaType(jdbcType)));
            column.setComment(columnComment);
            columnList.add(column);
        }
        return columnList;
    }

    @Override
    public List<String> enums() {
        return enumList;
    }

    @Override
    public String getFileName(String columnName, String columnType) {
        if ("TINYINT(1)".equalsIgnoreCase(columnType) && columnName.toLowerCase().startsWith("is_")) {
            columnName = columnName.substring(columnName.indexOf("_") + 1);
        }
        return StringUtil.toLowerCaseFirstOne(StringUtil.lineToHump(columnName));
    }

    @Override
    public String dataTypeToJdbcType(String dataType) {
        dataType = dataType.toUpperCase();
        switch (dataType) {
            case "INT":
                return "INTEGER";
            case "DATETIME":
                return "TIMESTAMP";
            case "TEXT":
            case "LONGTEXT":
                return "VARCHAR";
            default:
                break;
        }
        return dataType;
    }

    @Override
    public String jdbcTypeToJavaType(String jdbcType) {
        String javaType = "Object";
        switch (jdbcType.toUpperCase()) {
            case "CHAR":
            case "VARCHAR":
                return "String";
            case "DATE":
            case "TIMESTAMP":
                return "Date";
            case "BIT":
            case "TINYINT":
                return "Boolean";
            case "DOUBLE":
                return "Double";
            case "FLOAT":
                return "Float";
            case "INTEGER":
                return "Integer";
            case "BIGINT":
                return "Long";
            case "DECIMAL":
                return "BigDecimal";
            default:
                break;
        }
        return javaType;
    }

    @Override
    public String javaTypeToEnum(String tableName, String columnType, String javaType, String fieldName) {
        if ("TINYINT".equalsIgnoreCase(columnType) || "INT(4)".equalsIgnoreCase(columnType)) {
            javaType = StringUtil.toUpperCaseFirstOne(StringUtil.lineToHump(tableName)) + StringUtil.toUpperCaseFirstOne(fieldName);
            enumList.add(javaType);
        }
        return javaType;
    }
}