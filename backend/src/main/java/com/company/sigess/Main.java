package com.company.sigess;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class Main {
    private static final Properties props = new Properties();
    private static HttpServer server;

    public static void main(String[] args) throws IOException {
        // 1. Carga application.properties (Spring Boot style)
        loadConfig();

        // 2. Muestra startup info
        printStartupInfo();

        // 3. Test DB connection
//        testDatabase();

        // 4. Inicia server con puerto configurable
        int port = Integer.parseInt(get("server.port"));
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // 5. Routes
        server.createContext("/api/users", new com.company.sigess.controllers.UserController());
        server.setExecutor(null);
        server.start();

        System.out.println("✅ SIGESS Server ready! http://localhost:" + port);
    }

    private static void loadConfig() {
        try (var input = Main.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("⚠️ application.properties no encontrado, usando defaults");
                return;
            }
            props.load(input);
            System.out.println("📄 Config cargada OK");
        } catch (Exception e) {
            throw new RuntimeException("❌ Error cargando config", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    private static void printStartupInfo() {
        System.out.println("🚀 " + get("app.name") + " v" + get("app.version"));
        System.out.println("📡 Server: http://localhost:" + get("server.port"));
        //System.out.println("💾 DB: " + get("db.url", "jdbc:postgresql://localhost:5432/sigess"));
    }

//    private static void testDatabase() {
//        try (var conn = DatabaseConfig.getConnection()) {
//            System.out.println("🟢 Database conectado OK");
//        } catch (Exception e) {
//            System.err.println("🔴 Database error: " + e.getMessage());
//            System.err.println("💡 Ejecuta: psql -d sigess -f ../db/migrations/V1__schema.sql");
//        }
//    }


    public static HttpServer getServer() {
        return server;
    }
}
