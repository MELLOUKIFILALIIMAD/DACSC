package model.dao;

import java.io.IOException;

public class UserDAO extends BaseDAO {

    public UserDAO(ServeurTCP serveur) {
        super(serveur);
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

        serveur.envoyer("LOGOUT#" + login);
    }
}