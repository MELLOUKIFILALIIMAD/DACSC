package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectDB {

    public static final Object LOCK = new Object();

    private static Properties config;
    private static Connection connection;

    private ConnectDB() {}

    // À appeler une fois au démarrage avec le contenu de config.properties
    public static void init(Properties properties) {
        config = properties;
    }

    // Crée la connexion au premier appel, la recrée si elle est tombée
    public static Connection getConnection() throws SQLException {
        synchronized (LOCK) {
            if (config == null) {
                throw new SQLException("ConnectDB.init() n'a pas été appelé");
            }
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                connection = open();
            }
            return connection;
        }
    }

    // ---- Transactions : on regroupe plusieurs requêtes en une seule opération "tout ou rien" ----

    public static void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    public static void commit() throws SQLException {
        Connection con = getConnection();
        con.commit();
        con.setAutoCommit(true);
    }

    // Annule tout ce qui a été fait depuis beginTransaction() (ne lève jamais d'exception)
    public static void rollback() {
        synchronized (LOCK) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public static void close() {
        synchronized (LOCK) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
                connection = null;
            }
        }
    }

    private static Connection open() throws SQLException {
        String url = "jdbc:mysql://" + config.getProperty("db.host", "localhost")
                + ":" + config.getProperty("db.port", "3306")
                + "/" + config.getProperty("db.name")
                + "?useSSL=false&allowPublicKeyRetrieval=true";

        return DriverManager.getConnection(url,
                config.getProperty("db.user"),
                config.getProperty("db.password"));
    }

    // "abc" -> "%abc%" pour un LIKE ; les caractères % _ \ saisis par l'utilisateur sont neutralisés
    static String likePattern(String text) {
        String escaped = text.trim()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escaped + "%";
    }

    static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}