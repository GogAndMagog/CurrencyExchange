package org.fizz_buzz.model;

public class SQLConnectionManager {
    private final static String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";

    static {
        try {
            Class.forName(SQLITE_JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
