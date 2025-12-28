package com.company.sigess;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class Main {
    private static final Properties appProps = new Properties();  // application.properties// db/database.properties
    private static HttpServer server;

    public static void main(String[] args) throws IOException {

        loadAppConfig();
        printStartupInfo();


        // Inicia server con puerto configurable
        int port = Integer.parseInt(getApp("server.port"));
        server = HttpServer.create(new InetSocketAddress(port), 0);

        //  Routes
        server.createContext("/api/users", new com.company.sigess.controllers.UserController());
        server.createContext("/api/incidents", new com.company.sigess.controllers.UserController());
        server.setExecutor(null);
        server.start();

        System.out.println("SIGESS Server ready! http://localhost:" + port);
    }

    private static void loadAppConfig() {
        try (var input = Main.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("application.properties no encontrado");
                return;
            }
            appProps.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Error cargando app config", e);
        }
    }

    // App properties
    public static String getApp(String key) {
        return appProps.getProperty(key);
    }



    private static void printStartupInfo() {
        System.out.println("🚀 " + getApp("app.name") + " v" + getApp("app.version"));
        System.out.println("📡 Server: http://localhost:" + getApp("server.port"));

    }


}
