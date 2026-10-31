package model.dao;

import model.network.ClientTCP;

import java.io.IOException;

public class UserDAO extends BaseDAO {

    public UserDAO(ClientTCP client) {
        super(client);
    }

    // LOGIN#login#password  ->  LOGIN#ok  ou  LOGIN#ko#raison
    public void login(String login, String password) throws IOException {
        verifier(login, password);

        // ***** Construction de la requete *****
        String requete = "LOGIN#" + login + "#" + password;

        appeler("LOGIN", requete);
    }

    // LOGOUT#login  (le serveur ne répond pas)
    public void logout(String login) throws IOException {
        verifier(login);

        client.envoyer("LOGOUT#" + login);
    }
}