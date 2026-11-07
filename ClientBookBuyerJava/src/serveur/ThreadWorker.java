
package serveur;

import model.dao.ConnectDB;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThreadWorker extends Thread {

    private final FileAttente file;

    public ThreadWorker(String nom, FileAttente file) {
        super(nom);
        this.file = file;
    }

    @Override
    public void run() {
        while (true) {
            Socket socket;

            try {
                socket = file.retirer();
            } catch (InterruptedException e) {
                return;
            }

            traiterRequete(socket);
        }
    }

    private void traiterRequete(Socket socket) {

        try (Socket s = socket) {

            s.setSoTimeout(0);

            InputStream entree = s.getInputStream();
            OutputStream sortie = s.getOutputStream();

            System.out.println("[" + getName() + "] Client connecté.");

            while (!s.isClosed()) {

                byte[] entete = new byte[4];
                int lu = lireCompletement(entree, entete);

                if (lu == -1) {
                    System.out.println("Client déconnecté.");
                    break;
                }

                String tailleTexte = new String(entete, StandardCharsets.US_ASCII);
                int taille;

                try {
                    taille = Integer.parseInt(tailleTexte);
                } catch (NumberFormatException e) {
                    System.err.println("En-tête invalide : " + tailleTexte);
                    break;
                }

                if (taille <= 0 || taille > 9999) {
                    System.err.println("Taille invalide : " + taille);
                    break;
                }

                byte[] donnees = new byte[taille];

                if (lireCompletement(entree, donnees) == -1) {
                    break;
                }

                String requete = new String(donnees, StandardCharsets.UTF_8);

                System.out.println("REQUETE RECUE : " + requete);

                String[] champs = requete.split("#", -1);

                if (champs.length == 0) {
                    break;
                }

                String commande = champs[0];
                String reponse = "";

                if (commande.equals("CLIENT")) {
                    reponse = gererClient(champs);

                } else if (commande.equals("SELECT_BOOK")) {

                    System.out.println("1 - Début SELECT_BOOK");

                    reponse = gererSelectBook(champs);

                    System.out.println("6 - Fin SELECT_BOOK : " + reponse);

                } else if (commande.equals("ADD_CADDY_ITEM")) {
                    reponse = gererAddCaddyItem(champs);

                } else if (commande.equals("GET_CADDY")) {
                    reponse = gererGetCaddy(champs);
                } else if (commande.equals("UPDATE_CADDY_ITEM")) {
                    reponse = gererUpdateCaddyItem(champs);
                } else if (commande.equals("CANCEL_CADDY")) {
                    reponse = gererCancelCaddy(champs);

                } else if (commande.equals("LOGOUT")) {
                    break;

                } else {
                    reponse = commande + "#ko#Commande inconnue";
                }

                byte[] reponseBytes = reponse.getBytes(StandardCharsets.UTF_8);

                if (reponseBytes.length > 9999) {
                    System.err.println("Réponse trop longue.");
                    break;
                }

                byte[] enteteReponse = String.format(
                                "%04d", reponseBytes.length)
                        .getBytes(StandardCharsets.US_ASCII);

                byte[] trameReponse = new byte[
                        enteteReponse.length + reponseBytes.length];

                System.arraycopy(enteteReponse, 0,
                        trameReponse, 0, enteteReponse.length);

                System.arraycopy(reponseBytes, 0,
                        trameReponse, enteteReponse.length,
                        reponseBytes.length);

                System.out.println("7 - Envoi de la réponse");

                sortie.write(trameReponse);
                sortie.flush();

                System.out.println("8 - Réponse envoyée !");
            }

        } catch (SocketTimeoutException e) {

            System.out.println("[" + getName() + "] Timeout : client inactif ou lecture incomplète.");

        } catch (IOException e) {

            System.err.println("[" + getName() + "] Erreur TCP : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int lireCompletement(InputStream entree, byte[] donnees) throws IOException {

        int total = 0;

        while (total < donnees.length) {

            int resultat = entree.read(
                    donnees, total, donnees.length - total);

            if (resultat == -1) {
                return -1;
            }

            total += resultat;
        }

        return total;
    }

    private String gererClient(String[] champs) {

        if (champs.length < 3) {
            return "CLIENT#ko#Parametres insuffisants";
        }

        String nom = champs[1];
        String prenom = champs[2];

        String sql = "SELECT id FROM clients WHERE name = ? AND first_name = ?";

        synchronized (ConnectDB.LOCK) {

            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {

                ps.setString(1, nom);
                ps.setString(2, prenom);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return "CLIENT#ok#" + rs.getInt("id");
                    }
                }

                String insertSql =
                        "INSERT INTO clients (name, first_name, address) VALUES (?, ?, ?)";

                try (PreparedStatement psInsert =
                             ConnectDB.getConnection().prepareStatement(
                                     insertSql,
                                     PreparedStatement.RETURN_GENERATED_KEYS)) {

                    psInsert.setString(1, nom);
                    psInsert.setString(2, prenom);
                    psInsert.setString(3, "");

                    psInsert.executeUpdate();

                    try (ResultSet rsKeys = psInsert.getGeneratedKeys()) {

                        if (rsKeys.next()) {
                            return "CLIENT#ok#" + rsKeys.getInt(1);
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return "CLIENT#ko#" + e.getMessage();
            }
        }

        return "CLIENT#ko#Erreur interne";
    }

    private String gererSelectBook(String[] champs) {

        if (champs.length < 5) {
            return "SELECT_BOOK#ko#Parametres insuffisants";
        }

        String titre = champs[1];
        String auteur = champs[2];
        String sujet = champs[3];
        String maxPrix = champs[4];

        double prixMax = 999999.0;

        try {
            if (maxPrix != null && !maxPrix.trim().isEmpty() && !maxPrix.equals("0")) {
                prixMax = Double.parseDouble(maxPrix);
            }
        } catch (NumberFormatException e) {
            return "SELECT_BOOK#ko#Prix maximum invalide";
        }

        StringBuilder sb = new StringBuilder("SELECT_BOOK#ok");

        String sql =
                "SELECT b.id, b.title, a.last_name, b.publish_year, b.price, b.stock_quantity " +
                        "FROM books b JOIN authors a ON b.author_id = a.id " +
                        "JOIN subjects s ON b.subject_id = s.id " +
                        "WHERE b.title LIKE ? AND a.last_name LIKE ? " +
                        "AND s.name LIKE ? AND b.price <= ?";

        synchronized (ConnectDB.LOCK) {

            try {

                System.out.println("2 - Obtention connexion MySQL");

                try (PreparedStatement ps =
                             ConnectDB.getConnection().prepareStatement(sql)) {

                    System.out.println("3 - PreparedStatement créé");

                    ps.setString(1, "%" + titre + "%");
                    ps.setString(2, "%" + auteur + "%");
                    ps.setString(3, "%" + sujet + "%");
                    ps.setDouble(4, prixMax);

                    System.out.println("4 - Paramètres définis");
                    System.out.println("5 - Exécution SQL");

                    try (ResultSet rs = ps.executeQuery()) {

                        System.out.println("6 - SQL exécuté");

                        while (rs.next()) {

                            System.out.println("Livre trouvé : " + rs.getString("title"));

                            sb.append("#").append(rs.getInt("id"))
                                    .append("#").append(rs.getString("title"))
                                    .append("#").append(rs.getString("last_name"))
                                    .append("#").append(rs.getInt("publish_year"))
                                    .append("#").append(rs.getDouble("price"))
                                    .append("#").append(rs.getInt("stock_quantity"));
                        }
                    }
                }

            } catch (Exception e) {

                System.err.println("Erreur SQL SELECT_BOOK : " + e.getMessage());
                e.printStackTrace();

                return "SELECT_BOOK#ko#Erreur SQL : " + e.getMessage();
            }
        }

        return sb.toString();
    }


    private String gererAddCaddyItem(String[] champs) {

        if (champs.length < 4) {
            return "ADD_CADDY_ITEM#ko#Parametres insuffisants";
        }

        try {
            int clientId = Integer.parseInt(champs[1]);
            int bookId = Integer.parseInt(champs[2]);
            int quantity = Integer.parseInt(champs[3]);

            if (quantity <= 0) {
                return "ADD_CADDY_ITEM#ko#Quantite invalide";
            }

            synchronized (ConnectDB.LOCK) {

                int caddyId = -1;

                String selectCaddy =
                        "SELECT id FROM caddies " +
                                "WHERE client_id = ? AND payed = FALSE " +
                                "ORDER BY id DESC LIMIT 1";

                try (PreparedStatement ps =
                             ConnectDB.getConnection().prepareStatement(selectCaddy)) {

                    ps.setInt(1, clientId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            caddyId = rs.getInt("id");
                        }
                    }
                }

                // Création d'un panier s'il n'existe pas
                if (caddyId == -1) {

                    String insertCaddy =
                            "INSERT INTO caddies (client_id, date, amount, payed) " +
                                    "VALUES (?, NOW(), 0, FALSE)";

                    try (PreparedStatement ps =
                                 ConnectDB.getConnection().prepareStatement(
                                         insertCaddy,
                                         PreparedStatement.RETURN_GENERATED_KEYS)) {

                        ps.setInt(1, clientId);
                        ps.executeUpdate();

                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) {
                                caddyId = rs.getInt(1);
                            }
                        }
                    }
                }

                if (caddyId == -1) {
                    return "ADD_CADDY_ITEM#ko#Impossible de creer le panier";
                }

                // Vérifier si le livre existe déjà dans le panier
                int itemId = -1;

                String selectItem =
                        "SELECT id FROM caddies_items " +
                                "WHERE caddy_id = ? AND book_id = ?";

                try (PreparedStatement ps =
                             ConnectDB.getConnection().prepareStatement(selectItem)) {

                    ps.setInt(1, caddyId);
                    ps.setInt(2, bookId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            itemId = rs.getInt("id");
                        }
                    }
                }

                if (itemId != -1) {

                    String updateItem =
                            "UPDATE caddies_items " +
                                    "SET quantity = quantity + ? WHERE id = ?";

                    try (PreparedStatement ps =
                                 ConnectDB.getConnection().prepareStatement(updateItem)) {

                        ps.setInt(1, quantity);
                        ps.setInt(2, itemId);
                        ps.executeUpdate();
                    }

                } else {

                    String insertItem =
                            "INSERT INTO caddies_items (caddy_id, book_id, quantity) " +
                                    "VALUES (?, ?, ?)";

                    try (PreparedStatement ps =
                                 ConnectDB.getConnection().prepareStatement(insertItem)) {

                        ps.setInt(1, caddyId);
                        ps.setInt(2, bookId);
                        ps.setInt(3, quantity);
                        ps.executeUpdate();
                    }
                }

                // Recalcul du montant du panier
                String updateAmount =
                        "UPDATE caddies SET amount = (" +
                                "SELECT COALESCE(SUM(ci.quantity * b.price), 0) " +
                                "FROM caddies_items ci " +
                                "JOIN books b ON b.id = ci.book_id " +
                                "WHERE ci.caddy_id = ?) WHERE id = ?";

                try (PreparedStatement ps =
                             ConnectDB.getConnection().prepareStatement(updateAmount)) {

                    ps.setInt(1, caddyId);
                    ps.setInt(2, caddyId);
                    ps.executeUpdate();
                }
            }

            return "ADD_CADDY_ITEM#ok";

        } catch (Exception e) {
            e.printStackTrace();
            return "ADD_CADDY_ITEM#ko#" + e.getMessage();
        }
    }


    private String gererGetCaddy(String[] champs) {

        if (champs.length < 2) {
            return "GET_CADDY#ko#Parametres insuffisants";
        }

        try {
            int clientId = Integer.parseInt(champs[1]);

            synchronized (ConnectDB.LOCK) {

                int caddyId = -1;
                double amount = 0;

                String selectCaddy =
                        "SELECT id, amount FROM caddies " +
                                "WHERE client_id = ? AND payed = FALSE " +
                                "ORDER BY id DESC LIMIT 1";

                try (PreparedStatement ps =
                             ConnectDB.getConnection().prepareStatement(selectCaddy)) {

                    ps.setInt(1, clientId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            caddyId = rs.getInt("id");
                            amount = rs.getDouble("amount");
                        }
                    }
                }

                StringBuilder sb = new StringBuilder("GET_CADDY#ok");
                sb.append("#").append(amount);

                if (caddyId == -1) {
                    return sb.toString();
                }

                String sql =
                        "SELECT b.id, b.title, ci.quantity, b.price " +
                                "FROM caddies_items ci " +
                                "JOIN books b ON b.id = ci.book_id " +
                                "WHERE ci.caddy_id = ?";

                try (PreparedStatement ps =
                             ConnectDB.getConnection().prepareStatement(sql)) {

                    ps.setInt(1, caddyId);

                    try (ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {

                            sb.append("#").append(rs.getInt("id"));
                            sb.append("#").append(rs.getString("title"));
                            sb.append("#").append(rs.getInt("quantity"));
                            sb.append("#").append(rs.getDouble("price"));
                        }
                    }
                }

                return sb.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "GET_CADDY#ko#" + e.getMessage();
        }
    }

    private String gererUpdateCaddyItem(String[] champs) {

        if (champs.length < 4) {
            return "UPDATE_CADDY_ITEM#ko#Parametres insuffisants";
        }

        try {
            int clientId = Integer.parseInt(champs[1]);
            int bookId = Integer.parseInt(champs[2]);
            int quantity = Integer.parseInt(champs[3]);

            if (quantity < 0) {
                return "UPDATE_CADDY_ITEM#ko#Quantite invalide";
            }

            synchronized (ConnectDB.LOCK) {

                int caddyId = -1;

                String sqlCaddy =
                        "SELECT id FROM caddies " +
                                "WHERE client_id = ? AND payed = FALSE " +
                                "ORDER BY id DESC LIMIT 1";

                try (PreparedStatement ps =
                             ConnectDB.getConnection().prepareStatement(sqlCaddy)) {

                    ps.setInt(1, clientId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            caddyId = rs.getInt("id");
                        }
                    }
                }

                if (caddyId == -1) {
                    return "UPDATE_CADDY_ITEM#ko#Panier introuvable";
                }

                if (quantity == 0) {

                    String deleteSql =
                            "DELETE FROM caddies_items " +
                                    "WHERE caddy_id = ? AND book_id = ?";

                    try (PreparedStatement ps =
                                 ConnectDB.getConnection().prepareStatement(deleteSql)) {

                        ps.setInt(1, caddyId);
                        ps.setInt(2, bookId);

                        if (ps.executeUpdate() == 0) {
                            return "UPDATE_CADDY_ITEM#ko#Livre introuvable";
                        }
                    }

                } else {

                    String updateSql =
                            "UPDATE caddies_items SET quantity = ? " +
                                    "WHERE caddy_id = ? AND book_id = ?";

                    try (PreparedStatement ps =
                                 ConnectDB.getConnection().prepareStatement(updateSql)) {

                        ps.setInt(1, quantity);
                        ps.setInt(2, caddyId);
                        ps.setInt(3, bookId);

                        if (ps.executeUpdate() == 0) {
                            return "UPDATE_CADDY_ITEM#ko#Livre introuvable";
                        }
                    }
                }

                String amountSql =
                        "UPDATE caddies SET amount = (" +
                                "SELECT COALESCE(SUM(ci.quantity * b.price), 0) " +
                                "FROM caddies_items ci " +
                                "JOIN books b ON b.id = ci.book_id " +
                                "WHERE ci.caddy_id = ?) WHERE id = ?";

                try (PreparedStatement ps =
                             ConnectDB.getConnection().prepareStatement(amountSql)) {

                    ps.setInt(1, caddyId);
                    ps.setInt(2, caddyId);
                    ps.executeUpdate();
                }
            }

            return "UPDATE_CADDY_ITEM#ok";

        } catch (Exception e) {
            e.printStackTrace();
            return "UPDATE_CADDY_ITEM#ko#" + e.getMessage();
        }
    }



    private String gererCancelCaddy(String[] champs) {

        if (champs.length < 2) {
            return "CANCEL_CADDY#ko#Parametres insuffisants";
        }

        try {
            int clientId = Integer.parseInt(champs[1]);

            System.out.println("CANCEL_CADDY : clientId = " + clientId);
            synchronized (ConnectDB.LOCK) {

                int caddyId = -1;

                String selectSql =
                        "SELECT id FROM caddies " +
                                "WHERE client_id = ? AND payed = FALSE " +
                                "ORDER BY id DESC LIMIT 1";

                try (PreparedStatement ps =
                             ConnectDB.getConnection().prepareStatement(selectSql)) {

                    ps.setInt(1, clientId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            caddyId = rs.getInt("id");
                            System.out.println("CANCEL_CADDY : caddyId = " + caddyId);
                        }
                    }
                }

                if (caddyId == -1) {
                    return "CANCEL_CADDY#ok";
                }

                // Supprimer les articles du panier
                String deleteSql =
                        "DELETE FROM caddies_items WHERE caddy_id = ?";

                try (PreparedStatement ps =
                             ConnectDB.getConnection().prepareStatement(deleteSql)) {

                    ps.setInt(1, caddyId);

                    int lignesSupprimees = ps.executeUpdate();

                    System.out.println("CANCEL_CADDY : lignes supprimées = " + lignesSupprimees);
                }

                // Remettre le montant à zéro
                String updateSql =
                        "UPDATE caddies SET amount = 0 WHERE id = ?";

                try (PreparedStatement ps =
                             ConnectDB.getConnection().prepareStatement(updateSql)) {

                    ps.setInt(1, caddyId);
                    ps.executeUpdate();
                }
            }

            return "CANCEL_CADDY#ok";

        } catch (Exception e) {
            e.printStackTrace();
            return "CANCEL_CADDY#ko#" + e.getMessage();
        }
    }

}
