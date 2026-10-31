package model.dao;

import model.entity.Caddy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class CaddyDAO {

    // CREATE : retourne l'id généré (et le met dans l'objet). Un nouveau panier a un montant de 0.
    public int insert(Caddy caddy) throws SQLException {
        String sql = "INSERT INTO caddies (client_id, date, amount, payed) VALUES (?, ?, 0, FALSE)";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection()
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, caddy.getClientId());
                ps.setTimestamp(2, Timestamp.valueOf(caddy.getDate()));
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        caddy.setId(keys.getInt(1));
                    }
                }
            }
        }
        return caddy.getId();
    }

    // READ : le panier EN COURS (non payé) du client, ou null s'il n'en a pas
    public Caddy findOpenByClient(int clientId) throws SQLException {
        String sql = "SELECT id, client_id, date, amount, payed FROM caddies "
                + "WHERE client_id = ? AND payed = FALSE ORDER BY id DESC LIMIT 1";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                ps.setInt(1, clientId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Caddy(
                                rs.getInt("id"),
                                rs.getInt("client_id"),
                                rs.getTimestamp("date").toLocalDateTime(),
                                rs.getDouble("amount"),
                                rs.getBoolean("payed"));
                    }
                }
            }
        }
        return null;
    }

    // UPDATE : recalcule le montant du panier = somme (quantité x prix) de ses lignes
    public void updateAmount(int caddyId) throws SQLException {
        String sql = "UPDATE caddies SET amount = ("
                + "SELECT COALESCE(SUM(ci.quantity * b.price), 0) "
                + "FROM caddies_items ci JOIN books b ON b.id = ci.book_id "
                + "WHERE ci.caddy_id = ?) WHERE id = ?";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                ps.setInt(1, caddyId);
                ps.setInt(2, caddyId);
                ps.executeUpdate();
            }
        }
    }

    // UPDATE : le panier devient "payé"
    public boolean markPayed(int caddyId) throws SQLException {
        String sql = "UPDATE caddies SET payed = TRUE WHERE id = ? AND payed = FALSE";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                ps.setInt(1, caddyId);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // DELETE (les lignes du panier doivent avoir été supprimées avant : clé étrangère)
    public boolean delete(int caddyId) throws SQLException {
        String sql = "DELETE FROM caddies WHERE id = ?";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                ps.setInt(1, caddyId);
                return ps.executeUpdate() > 0;
            }
        }
    }
}