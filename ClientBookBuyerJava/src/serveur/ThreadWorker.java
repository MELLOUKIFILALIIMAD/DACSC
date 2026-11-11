package serveur;

import model.dao.ConnectDB;
import model.protocol.Reponse;
import model.protocol.Requete;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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

            ObjectOutputStream sortie = new ObjectOutputStream(s.getOutputStream());
            sortie.flush();

            ObjectInputStream entree = new ObjectInputStream(s.getInputStream());
            entree.setObjectInputFilter(ObjectInputFilter.Config.createFilter("model.protocol.*;java.lang.*;!*"));

            Object recu = entree.readObject();

            if (!(recu instanceof Requete)) {
                return;
            }

            String[] champs = ((Requete) recu).getChamps();

            if (champs == null || champs.length == 0 || champs[0] == null) {
                return;
            }

            System.out.println("[" + getName() + "] REQUETE RECUE : " + champs[0]);

            String reponse = dispatcher(champs);

            sortie.writeObject(new Reponse(reponse.split("#", -1)));
            sortie.flush();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[" + getName() + "] Erreur : " + e.getMessage());
        }
    }

    private String dispatcher(String[] champs) {

        String commande = champs[0];

        try {
            if (commande.equals("CLIENT")) {
                return gererClient(champs);

            } else if (commande.equals("SELECT_BOOK")) {
                return gererSelectBook(champs);

            } else if (commande.equals("ADD_CADDY_ITEM")) {
                return gererAddCaddyItem(champs);

            } else if (commande.equals("GET_CADDY")) {
                return gererGetCaddy(champs);

            } else if (commande.equals("DEL_CADDY_ITEM")) {
                return gererDelCaddyItem(champs);

            } else if (commande.equals("CANCEL_CADDY")) {
                return gererCancelCaddy(champs);

            } else if (commande.equals("PAY_CADDY")) {
                return gererPayCaddy(champs);

            } else if (commande.equals("CLEAR_CADDY")) {
                return gererClearCaddy(champs);
            }

            return commande + "#ko#Commande inconnue";

        } catch (SQLException e) {
            e.printStackTrace();
            return commande + "#ko#" + e.getMessage();
        }
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

        if (champs.length < 6) {
            return "SELECT_BOOK#ko#Parametres insuffisants";
        }

        String titre = champs[1];
        String auteur = champs[2];
        String sujet = champs[3];

        double prixMin = 0;
        double prixMax = Double.MAX_VALUE;

        try {
            if (!champs[4].trim().isEmpty()) {
                prixMin = Double.parseDouble(champs[4]);
            }

            if (!champs[5].trim().isEmpty()) {
                if (!champs[5].equals("0")) {
                    prixMax = Double.parseDouble(champs[5]);
                }
            }

            if (prixMin < 0 || prixMax < 0 || prixMin > prixMax) {
                return "SELECT_BOOK#ko#Prix invalides";
            }

        } catch (NumberFormatException e) {
            return "SELECT_BOOK#ko#Prix invalides";
        }

        StringBuilder sb = new StringBuilder("SELECT_BOOK#ok");

        String sql =
                "SELECT b.id, b.title, a.last_name, b.publish_year, " +
                        "b.price, b.stock_quantity " +
                        "FROM books b " +
                        "JOIN authors a ON b.author_id = a.id " +
                        "JOIN subjects s ON b.subject_id = s.id " +
                        "WHERE b.title LIKE ? " +
                        "AND a.last_name LIKE ? " +
                        "AND s.name LIKE ? " +
                        "AND b.price >= ? " +
                        "AND b.price <= ?";

        synchronized (ConnectDB.LOCK) {

            try (PreparedStatement ps =
                         ConnectDB.getConnection().prepareStatement(sql)) {

                ps.setString(1, "%" + titre + "%");
                ps.setString(2, "%" + auteur + "%");
                ps.setString(3, "%" + sujet + "%");
                ps.setDouble(4, prixMin);
                ps.setDouble(5, prixMax);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        sb.append("#").append(rs.getInt("id"))
                                .append("#").append(rs.getString("title"))
                                .append("#").append(rs.getString("last_name"))
                                .append("#").append(rs.getInt("publish_year"))
                                .append("#").append(rs.getDouble("price"))
                                .append("#").append(rs.getInt("stock_quantity"));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "SELECT_BOOK#ko#Erreur SQL : " + e.getMessage();
            }
        }

        return sb.toString();
    }


    private String gererAddCaddyItem(String[] champs) throws SQLException {

        if (champs.length < 4) {
            return "ADD_CADDY_ITEM#ko#Parametres insuffisants";
        }

        java.sql.Connection connexion = ConnectDB.getConnection();

        try {
            int clientId = Integer.parseInt(champs[1]);
            int bookId = Integer.parseInt(champs[2]);
            int quantity = Integer.parseInt(champs[3]);

            if (quantity <= 0) {
                return "ADD_CADDY_ITEM#ko#Quantite invalide";
            }

            synchronized (ConnectDB.LOCK) {

                connexion.setAutoCommit(false);

                try {
                    // Vérifier et verrouiller le stock du livre.
                    String sqlStock =
                            "SELECT stock_quantity FROM books WHERE id = ? FOR UPDATE";

                    int stock;

                    try (PreparedStatement ps = connexion.prepareStatement(sqlStock)) {
                        ps.setInt(1, bookId);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                connexion.rollback();
                                return "ADD_CADDY_ITEM#ko#Livre introuvable";
                            }

                            stock = rs.getInt("stock_quantity");
                        }
                    }

                    if (stock < quantity) {
                        connexion.rollback();
                        return "ADD_CADDY_ITEM#ko#Stock insuffisant";
                    }

                    // Trouver le panier ouvert du client.
                    int caddyId = -1;

                    String sqlCaddy =
                            "SELECT id FROM caddies " +
                                    "WHERE client_id = ? AND payed = FALSE " +
                                    "ORDER BY id DESC LIMIT 1";

                    try (PreparedStatement ps = connexion.prepareStatement(sqlCaddy)) {
                        ps.setInt(1, clientId);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                caddyId = rs.getInt("id");
                            }
                        }
                    }

                    // Créer un panier s'il n'existe pas.
                    if (caddyId == -1) {

                        String insertCaddy =
                                "INSERT INTO caddies (client_id, date, amount, payed) " +
                                        "VALUES (?, NOW(), 0, FALSE)";

                        try (PreparedStatement ps = connexion.prepareStatement(
                                insertCaddy, PreparedStatement.RETURN_GENERATED_KEYS)) {

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
                        connexion.rollback();
                        return "ADD_CADDY_ITEM#ko#Impossible de creer le panier";
                    }

                    // Diminuer le stock immédiatement.
                    String updateStock =
                            "UPDATE books SET stock_quantity = stock_quantity - ? " +
                                    "WHERE id = ? AND stock_quantity >= ?";

                    try (PreparedStatement ps = connexion.prepareStatement(updateStock)) {
                        ps.setInt(1, quantity);
                        ps.setInt(2, bookId);
                        ps.setInt(3, quantity);

                        if (ps.executeUpdate() != 1) {
                            connexion.rollback();
                            return "ADD_CADDY_ITEM#ko#Stock insuffisant";
                        }
                    }

                    // Vérifier si le livre est déjà dans le panier.
                    String selectItem =
                            "SELECT id FROM caddies_items " +
                                    "WHERE caddy_id = ? AND book_id = ?";

                    int itemId = -1;

                    try (PreparedStatement ps = connexion.prepareStatement(selectItem)) {
                        ps.setInt(1, caddyId);
                        ps.setInt(2, bookId);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                itemId = rs.getInt("id");
                            }
                        }
                    }

                    // Augmenter la quantité ou créer une ligne.
                    if (itemId != -1) {

                        String updateItem =
                                "UPDATE caddies_items SET quantity = quantity + ? " +
                                        "WHERE id = ?";

                        try (PreparedStatement ps = connexion.prepareStatement(updateItem)) {
                            ps.setInt(1, quantity);
                            ps.setInt(2, itemId);
                            ps.executeUpdate();
                        }

                    } else {

                        String insertItem =
                                "INSERT INTO caddies_items (caddy_id, book_id, quantity) " +
                                        "VALUES (?, ?, ?)";

                        try (PreparedStatement ps = connexion.prepareStatement(insertItem)) {
                            ps.setInt(1, caddyId);
                            ps.setInt(2, bookId);
                            ps.setInt(3, quantity);
                            ps.executeUpdate();
                        }
                    }

                    // Recalculer le montant.
                    String updateAmount =
                            "UPDATE caddies SET amount = (" +
                                    "SELECT COALESCE(SUM(ci.quantity * b.price), 0) " +
                                    "FROM caddies_items ci " +
                                    "JOIN books b ON b.id = ci.book_id " +
                                    "WHERE ci.caddy_id = ?) WHERE id = ?";

                    try (PreparedStatement ps = connexion.prepareStatement(updateAmount)) {
                        ps.setInt(1, caddyId);
                        ps.setInt(2, caddyId);
                        ps.executeUpdate();
                    }

                    connexion.commit();
                    return "ADD_CADDY_ITEM#ok";

                } catch (Exception e) {
                    connexion.rollback();
                    throw e;

                } finally {
                    connexion.setAutoCommit(true);
                }
            }

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

    private String gererCancelCaddy(String[] champs) {

        if (champs.length < 2) {
            return "CANCEL_CADDY#ko#Parametres insuffisants";
        }

        java.sql.Connection connexion = null;

        try {
            int clientId = Integer.parseInt(champs[1]);

            synchronized (ConnectDB.LOCK) {

                connexion = ConnectDB.getConnection();
                connexion.setAutoCommit(false);

                try {
                    int caddyId = -1;

                    String selectCaddy =
                            "SELECT id FROM caddies " +
                                    "WHERE client_id = ? AND payed = FALSE " +
                                    "ORDER BY id DESC LIMIT 1";

                    try (PreparedStatement ps = connexion.prepareStatement(selectCaddy)) {

                        ps.setInt(1, clientId);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                caddyId = rs.getInt("id");
                            }
                        }
                    }

                    if (caddyId == -1) {
                        connexion.commit();
                        return "CANCEL_CADDY#ok";
                    }

                    // Récupérer les livres et leurs quantités
                    java.util.ArrayList<int[]> articles = new java.util.ArrayList<>();

                    String selectItems =
                            "SELECT book_id, quantity FROM caddies_items " +
                                    "WHERE caddy_id = ?";

                    try (PreparedStatement ps = connexion.prepareStatement(selectItems)) {

                        ps.setInt(1, caddyId);

                        try (ResultSet rs = ps.executeQuery()) {

                            while (rs.next()) {
                                int bookId = rs.getInt("book_id");
                                int quantity = rs.getInt("quantity");

                                articles.add(new int[]{bookId, quantity});
                            }
                        }
                    }

                    // Restituer les quantités au stock
                    String updateStock =
                            "UPDATE books SET stock_quantity = stock_quantity + ? " +
                                    "WHERE id = ?";

                    try (PreparedStatement ps = connexion.prepareStatement(updateStock)) {

                        for (int[] article : articles) {

                            ps.setInt(1, article[1]);
                            ps.setInt(2, article[0]);

                            ps.executeUpdate();
                        }
                    }

                    // Supprimer les articles du panier
                    String deleteItems =
                            "DELETE FROM caddies_items WHERE caddy_id = ?";

                    try (PreparedStatement ps = connexion.prepareStatement(deleteItems)) {
                        ps.setInt(1, caddyId);
                        ps.executeUpdate();
                    }

                    // Remettre le montant à zéro
                    String updateAmount =
                            "UPDATE caddies SET amount = 0 WHERE id = ?";

                    try (PreparedStatement ps = connexion.prepareStatement(updateAmount)) {
                        ps.setInt(1, caddyId);
                        ps.executeUpdate();
                    }

                    connexion.commit();

                    System.out.println("CANCEL_CADDY : stock restauré pour "
                            + articles.size() + " livres.");

                    return "CANCEL_CADDY#ok";

                } catch (Exception e) {
                    connexion.rollback();
                    throw e;

                } finally {
                    connexion.setAutoCommit(true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "CANCEL_CADDY#ko#" + e.getMessage();
        }
    }

    private String gererDelCaddyItem(String[] champs) {

        if (champs.length < 4) {
            return "DEL_CADDY_ITEM#ko#Parametres insuffisants";
        }

        java.sql.Connection connexion = null;

        try {
            int clientId = Integer.parseInt(champs[1]);
            int bookId = Integer.parseInt(champs[2]);
            int quantity = Integer.parseInt(champs[3]);

            if (quantity <= 0) {
                return "DEL_CADDY_ITEM#ko#Quantite invalide";
            }

            synchronized (ConnectDB.LOCK) {

                connexion = ConnectDB.getConnection();
                connexion.setAutoCommit(false);

                try {
                    int caddyId = -1;

                    String selectCaddy =
                            "SELECT id FROM caddies " +
                                    "WHERE client_id = ? AND payed = FALSE " +
                                    "ORDER BY id DESC LIMIT 1";

                    try (PreparedStatement ps = connexion.prepareStatement(selectCaddy)) {
                        ps.setInt(1, clientId);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                caddyId = rs.getInt("id");
                            }
                        }
                    }

                    if (caddyId == -1) {
                        connexion.rollback();
                        return "DEL_CADDY_ITEM#ko#Panier introuvable";
                    }

                    int presente = 0;

                    String selectItem =
                            "SELECT quantity FROM caddies_items " +
                                    "WHERE caddy_id = ? AND book_id = ?";

                    try (PreparedStatement ps = connexion.prepareStatement(selectItem)) {
                        ps.setInt(1, caddyId);
                        ps.setInt(2, bookId);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                presente = rs.getInt("quantity");
                            }
                        }
                    }

                    if (presente <= 0) {
                        connexion.rollback();
                        return "DEL_CADDY_ITEM#ko#Livre absent du panier";
                    }

                    int retiree = Math.min(quantity, presente);

                    if (retiree == presente) {

                        String deleteSql =
                                "DELETE FROM caddies_items " +
                                        "WHERE caddy_id = ? AND book_id = ?";

                        try (PreparedStatement ps = connexion.prepareStatement(deleteSql)) {
                            ps.setInt(1, caddyId);
                            ps.setInt(2, bookId);
                            ps.executeUpdate();
                        }

                    } else {

                        String updateSql =
                                "UPDATE caddies_items SET quantity = ? " +
                                        "WHERE caddy_id = ? AND book_id = ?";

                        try (PreparedStatement ps = connexion.prepareStatement(updateSql)) {
                            ps.setInt(1, presente - retiree);
                            ps.setInt(2, caddyId);
                            ps.setInt(3, bookId);
                            ps.executeUpdate();
                        }
                    }

                    String updateStock =
                            "UPDATE books SET stock_quantity = stock_quantity + ? WHERE id = ?";

                    try (PreparedStatement ps = connexion.prepareStatement(updateStock)) {
                        ps.setInt(1, retiree);
                        ps.setInt(2, bookId);
                        ps.executeUpdate();
                    }

                    String updateAmount =
                            "UPDATE caddies SET amount = (" +
                                    "SELECT COALESCE(SUM(ci.quantity * b.price), 0) " +
                                    "FROM caddies_items ci " +
                                    "JOIN books b ON b.id = ci.book_id " +
                                    "WHERE ci.caddy_id = ?) WHERE id = ?";

                    try (PreparedStatement ps = connexion.prepareStatement(updateAmount)) {
                        ps.setInt(1, caddyId);
                        ps.setInt(2, caddyId);
                        ps.executeUpdate();
                    }

                    connexion.commit();
                    return "DEL_CADDY_ITEM#ok";

                } catch (Exception e) {
                    connexion.rollback();
                    throw e;

                } finally {
                    connexion.setAutoCommit(true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "DEL_CADDY_ITEM#ko#" + e.getMessage();
        }
    }


    private String gererClearCaddy(String[] champs) {

        if (champs.length < 2) {
            return "CLEAR_CADDY#ko#Parametres insuffisants";
        }

        try {
            int clientId = Integer.parseInt(champs[1]);

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

                if (caddyId == -1) {
                    return "CLEAR_CADDY#ko#Panier introuvable";
                }

                int nbItems = 0;

                try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(
                        "SELECT COUNT(*) FROM caddies_items WHERE caddy_id = ?")) {

                    ps.setInt(1, caddyId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            nbItems = rs.getInt(1);
                        }
                    }
                }

                if (nbItems == 0) {
                    return "CLEAR_CADDY#ko#Panier vide";
                }

                String updateStock =
                        "UPDATE books b " +
                                "JOIN caddies_items ci ON b.id = ci.book_id " +
                                "SET b.stock_quantity = b.stock_quantity + ci.quantity " +
                                "WHERE ci.caddy_id = ?";

                try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(updateStock)) {
                    ps.setInt(1, caddyId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(
                        "DELETE FROM caddies_items WHERE caddy_id = ?")) {

                    ps.setInt(1, caddyId);

                    if (ps.executeUpdate() == 0) {
                        return "CLEAR_CADDY#ko#Suppression impossible";
                    }
                }

                try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(
                        "UPDATE caddies SET amount = 0 WHERE id = ?")) {

                    ps.setInt(1, caddyId);
                    ps.executeUpdate();
                }
            }

            return "CLEAR_CADDY#ok";

        } catch (Exception e) {
            e.printStackTrace();
            return "CLEAR_CADDY#ko#" + e.getMessage();
        }
    }


    private String gererPayCaddy(String[] champs) {

        if (champs.length < 2) {
            return "PAY_CADDY#ko#Parametres insuffisants";
        }

        try {
            int clientId = Integer.parseInt(champs[1]);

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

                if (caddyId == -1) {
                    return "PAY_CADDY#ko#Panier introuvable";
                }

                int nbItems = 0;

                try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(
                        "SELECT COUNT(*) FROM caddies_items WHERE caddy_id = ?")) {

                    ps.setInt(1, caddyId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            nbItems = rs.getInt(1);
                        }
                    }
                }

                if (nbItems == 0) {
                    return "PAY_CADDY#ko#Panier vide";
                }

                try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(
                        "UPDATE caddies SET payed = TRUE WHERE id = ? AND payed = FALSE")) {

                    ps.setInt(1, caddyId);

                    if (ps.executeUpdate() != 1) {
                        return "PAY_CADDY#ko#Paiement impossible";
                    }
                }
            }

            return "PAY_CADDY#ok";

        } catch (Exception e) {
            e.printStackTrace();
            return "PAY_CADDY#ko#" + e.getMessage();
        }
    }
}