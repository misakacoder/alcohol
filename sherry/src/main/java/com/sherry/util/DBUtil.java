package com.sherry.util;

import com.sherry.enums.Database;

public class DBUtil {

    public static Database getDatabaseByUrl(String url) {
        if (StringUtil.isNotBlank(url)) {
            if (url.contains(":mysql:")) {
                return Database.MYSQL;
            } else if (url.contains(":oracle:")) {
                return Database.ORACLE;
            } else if (url.contains(":sqlserver:")) {
                return Database.SQL_SERVER;
            } else {
                throw new RuntimeException("Unsupported database");
            }
        }
        return null;
    }

    public static String getDatabaseNameByUrl(String url) {
        Database database = getDatabaseByUrl(url);
        if (database != null) {
            if (database == Database.MYSQL) {
                int end = url.length();
                if (url.contains("?")) {
                    end = url.indexOf("?");
                }
                return url.substring(url.lastIndexOf("/") + 1, end);
            }
        }
        return null;
    }
}
