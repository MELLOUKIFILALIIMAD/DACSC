package model.dao;

import model.network.ClientTCP;

import java.io.IOException;

public abstract class BaseDAO {

    protected final ClientTCP client;

    protected BaseDAO(ClientTCP client) {
        this.client = client;
    }

    protected String[] appeler(String nom, String requete) throws IOException {
        String reponse = client.echanger(requete);

        String[] champs = reponse.split("#", -1);

        if (champs.length < 2 || !champs[0].equals(nom)) {
            throw new IOException("Réponse inattendue du serveur pour " + nom);
        }

        if (!champs[1].equals("ok")) {
            throw new IOException(champs.length > 2 ? champs[2] : "Erreur inconnue");
        }

        return champs;
    }

    protected void verifier(String... valeurs) throws IOException {
        for (String valeur : valeurs) {
            if (valeur == null || valeur.contains("#")) {
                throw new IOException("Champ invalide (le caractère '#' est interdit)");
            }
        }
    }
}