package com.company.sigess.repositories;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConn {

    private static DBConn instance;
    private Connection connection;

    private DBConn() {
        try {
            Properties props = new Properties();

            InputStream input = DBConn.class
                    .getClassLoader()
                    .getResourceAsStream("db/database.properties");

            if (input == null) {
                throw new RuntimeException("❌ db/database.properties not found in classpath");
            }

            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            this.connection = DriverManager.getConnection(url, user, password);

            System.out.println("✅ Database connection established");

        } catch (Exception e) {
            throw new RuntimeException("❌ Error initializing DB connection", e);
        }
    }

    public static DBConn getInstance() {
        if (instance == null) {
            instance = new DBConn();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}


