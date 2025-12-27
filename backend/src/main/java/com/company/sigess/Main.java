package com.company.sigess;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main {
    private static final Properties appProps = new Properties();  // application.properties
    private static final Properties dbProps = new Properties();   // db/database.properties
    private static HttpServer server;

    public static void main(String[] args) throws IOException {
        // 1. Carga configs (app + DB separadas)
        loadAppConfig();
        loadDbConfig();

        // 2. Muestra startup info
        printStartupInfo();


        // 3. Inicia server con puerto configurable
        int port = Integer.parseInt(getApp("server.port"));
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // 4. Routes
        server.createContext("/api/users", new com.company.sigess.controllers.UserController());
        server.createContext("/api/health", Main::handleHealth);
        server.setExecutor(null);
        server.start();

        System.out.println("SIGESS Server ready! http://localhost:" + port);
    }

    private static void loadAppConfig() {
        try (var input = Main.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("⚠️ application.properties no encontrado");
                return;
            }
            appProps.load(input);
            System.out.println("📄 App config cargada OK");
        } catch (Exception e) {
            throw new RuntimeException("❌ Error cargando app config", e);
        }
    }

    private static void loadDbConfig() {
        try (var input = Main.class.getClassLoader()
                .getResourceAsStream("db/database.properties")) {
            if (input == null) {
                System.err.println("⚠️ db/database.properties no encontrado");
                return;
            }
            dbProps.load(input);
            System.out.println("💾 DB config cargada OK");
        } catch (Exception e) {
            throw new RuntimeException("❌ Error cargando DB config", e);
        }
    }

    // App properties
    public static String getApp(String key) {
        return appProps.getProperty(key);
    }

    // DB properties
    public static String getDb(String key) {
        return dbProps.getProperty(key);
    }

    private static void printStartupInfo() {
        System.out.println("🚀 " + getApp("app.name") + " v" + getApp("app.version"));
        System.out.println("📡 Server: http://localhost:" + getApp("server.port"));
        System.out.println("💾 DB: " + getDb("db.url"));
    }



    private static void handleHealth(HttpExchange exchange) throws IOException {
        String response = "{\"status\": \"UP\", \"app\": \"" + getApp("app.name") + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    public static HttpServer getServer() {
        return server;
    }
}
