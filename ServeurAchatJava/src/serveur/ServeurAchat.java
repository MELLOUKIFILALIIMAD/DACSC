package serveur;

import model.dao.BookDAO;
import model.dao.CaddyDAO;
import model.dao.CaddyItemDAO;
import model.dao.ClientDAO;
import model.dao.ConnectDB;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Properties;

public class ServeurAchat {

    public static void main(String[] args) {
        System.out.println("(SERVEUR ACHAT) Démarrage");

        //  Lecture du fichier de configuration (comme LireConfiguration en C) 
        Properties config = new Properties();
        try (FileInputStream fichier = new FileInputStream("config.properties")) {
            config.load(fichier);
        } catch (IOException e) {
            System.err.println("(SERVEUR ACHAT) Impossible de lire config.properties : " + e.getMessage());
            return;
        }

        int port;
        int nbThreads;
        try {
            port = Integer.parseInt(config.getProperty("PORT_PAYMENT").trim());
            nbThreads = Integer.parseInt(config.getProperty("NB_THREADS").trim());
        } catch (NumberFormatException | NullPointerException e) {
            System.err.println("(SERVEUR ACHAT) PORT_PAYMENT ou NB_THREADS manquant / invalide dans config.properties");
            return;
        }

        //  Connexion UNIQUE à la base de données 
        ConnectDB.init(config);
        try {
            ConnectDB.getConnection();
        } catch (SQLException e) {
            System.err.println("(SERVEUR ACHAT) Connexion à la base impossible : " + e.getMessage());
            return;
        }
        System.out.println("(SERVEUR ACHAT) Connecté à la base de données");

        //  Équivalent de HandlerSIGINT : à Ctrl+C on ferme proprement la connexion BD 
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("(SERVEUR ACHAT) Arrêt : fermeture de la base de données");
                ConnectDB.close();
            }
        });

        //  Création du pool de threads 
        FileAttente file = new FileAttente();
        for (int i = 0; i < nbThreads; i++) {
            ThreadWorker worker = new ThreadWorker("Worker-" + (i + 1), file);
            worker.setDaemon(true);
            worker.start();
        }

        //  Attente des clients 
        try (ServerSocket serveur = new ServerSocket(port)) {
            System.out.println("(SERVEUR ACHAT) En attente de requêtes sur le port " + port
                    + " (" + nbThreads + " threads)");

            while (true) {
                Socket socket = serveur.accept();
                file.ajouter(socket);       // un thread du pool va la prendre en charge
            }
        } catch (IOException e) {
            System.err.println("(SERVEUR ACHAT) Erreur serveur : " + e.getMessage());
        }
    }
}