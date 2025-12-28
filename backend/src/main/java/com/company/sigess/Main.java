package com.company.sigess;

import java.io.IOException;

import java.util.Properties;

public class Main {
    private static final Properties appProps = new Properties();  // application.properties// db/database.properties


    public static void main(String[] args) throws IOException {
        loadAppConfig();
        printStartupInfo();

        System.out.println("SIGESS Backend (Servlet Mode)");
        System.out.println("Para ejecutar, despliegue el archivo WAR en un contenedor de Servlets (ej. Tomcat)");
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
