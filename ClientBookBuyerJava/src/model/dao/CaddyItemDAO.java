package model.dao;

import model.entity.CaddyItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CaddyItemDAO {

    private static final String SELECT = "SELECT id, caddy_id, book_id, quantity FROM caddies_items";

    // CREATE : retourne l'id généré (et le met dans l'objet)
    public int insert(CaddyItem item) throws SQLException {
        String sql = "INSERT INTO caddies_items (caddy_id, book_id, quantity) VALUES (?, ?, ?)";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection()
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, item.getCaddyId());
                ps.setInt(2, item.getBookId());
                ps.setInt(3, item.getQuantity());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        item.setId(keys.getInt(1));
                    }
                }
            }
        }
        return item.getId();
    }

    // READ : la ligne du panier pour ce livre, ou null si le livre n'est pas dans le panier
    public CaddyItem find(int caddyId, int bookId) throws SQLException {
        List<Object> params = new ArrayList<>();
        params.add(caddyId);
        params.add(bookId);

        List<CaddyItem> result = query(SELECT + " WHERE caddy_id = ? AND book_id = ?", params);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    // READ : toutes les lignes d'un panier
    public List<CaddyItem> findByCaddy(int caddyId) throws SQLException {
        List<Object> params = new ArrayList<>();
        params.add(caddyId);

        return query(SELECT + " WHERE caddy_id = ?", params);
    }

    // UPDATE : change la quantité d'une ligne
    public boolean updateQuantity(int itemId, int quantity) throws SQLException {
        String sql = "UPDATE caddies_items SET quantity = ? WHERE id = ?";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                ps.setInt(1, quantity);
                ps.setInt(2, itemId);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // DELETE : une ligne
    public boolean delete(int itemId) throws SQLException {
        String sql = "DELETE FROM caddies_items WHERE id = ?";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                ps.setInt(1, itemId);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // DELETE : toutes les lignes d'un panier
    public void deleteByCaddy(int caddyId) throws SQLException {
        String sql = "DELETE FROM caddies_items WHERE caddy_id = ?";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                ps.setInt(1, caddyId);
                ps.executeUpdate();
            }
        }
    }

    private List<CaddyItem> query(String sql, List<Object> params) throws SQLException {
        List<CaddyItem> items = new ArrayList<>();

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(new CaddyItem(
                                rs.getInt("id"),
                                rs.getInt("caddy_id"),
                                rs.getInt("book_id"),
                                rs.getInt("quantity")));
                    }
                }
            }
        }
        return items;
    }
}