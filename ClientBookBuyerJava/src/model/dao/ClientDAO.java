package model.dao;

import model.entity.Client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    private static final String SELECT = "SELECT id, name, first_name, address FROM clients";

    // CREATE : retourne l'id généré = le numéro de client (et le met dans l'objet)
    public int insert(Client client) throws SQLException {
        String sql = "INSERT INTO clients (name, first_name, address) VALUES (?, ?, ?)";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection()
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, client.getName());
                ps.setString(2, client.getFirstName());
                ps.setString(3, client.getAddress());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        client.setId(keys.getInt(1));
                    }
                }
            }
        }
        return client.getId();
    }

    // READ : null si le numéro de client n'existe pas
    public Client findById(int id) throws SQLException {
        List<Object> params = new ArrayList<>();
        params.add(id);

        List<Client> result = query(SELECT + " WHERE id = ?", params);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    // READ : recherche par nom ET prénom exacts (MySQL ne tient pas compte de la casse). null si absent.
    public Client findByName(String name, String firstName) throws SQLException {
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(firstName);

        List<Client> result = query(SELECT + " WHERE name = ? AND first_name = ?", params);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    // UPDATE : true si une ligne a été modifiée
    public boolean update(Client client) throws SQLException {
        String sql = "UPDATE clients SET name = ?, first_name = ?, address = ? WHERE id = ?";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                ps.setString(1, client.getName());
                ps.setString(2, client.getFirstName());
                ps.setString(3, client.getAddress());
                ps.setInt(4, client.getId());
                return ps.executeUpdate() > 0;
            }
        }
    }

    // DELETE : true si une ligne a été supprimée
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM clients WHERE id = ?";

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        }
    }

    private List<Client> query(String sql, List<Object> params) throws SQLException {
        List<Client> clients = new ArrayList<>();

        synchronized (ConnectDB.LOCK) {
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        clients.add(new Client(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("first_name"),
                                rs.getString("address")));
                    }
                }
            }
        }
        return clients;
    }
}