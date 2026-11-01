package serveur;

import model.dao.ConnectDB;
import model.viewmodel.GUI.JDialogServeur;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Properties;

public class ServeurAchat {

    public static void main(String[] args) {
        System.out.println("(SERVEUR ACHAT) Démarrage");

        // 1. Afficher le JDialog
        final String[] ip = {null};

        try {
            SwingUtilities.invokeAndWait(() -> {
                JDialogServeur dialog = new JDialogServeur(null);
                dialog.setVisible(true);
                ip[0] = dialog.getIp();
            });
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 2. Vérifier si un serveur a été choisi
        if (ip[0] == null) {
            System.out.println("(SERVEUR ACHAT) Aucun serveur sélectionné");
            return;
        }

        // 3. Lire config.properties
        Properties config = new Properties();

        try (FileInputStream fichier =
                     new FileInputStream("config.properties")) {
            config.load(fichier);
        } catch (IOException e) {
            System.err.println("(SERVEUR ACHAT) Erreur : " + e.getMessage());
            return;
        }

        // 4. Modifier DB_HOST selon le bouton sélectionné
        config.setProperty("DB_HOST", ip[0]);

        // 5. Sauvegarder la nouvelle configuration
        try (FileOutputStream fichier =
                     new FileOutputStream("config.properties")) {
            config.store(fichier, "Configuration Serveur Achat");
        } catch (IOException e) {
            System.err.println("(SERVEUR ACHAT) Erreur sauvegarde : " + e.getMessage());
            return;
        }

        System.out.println("(SERVEUR ACHAT) IP MySQL : " + ip[0]);

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