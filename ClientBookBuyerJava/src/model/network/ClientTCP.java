package model.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ClientTCP {

    private Socket socket;
    private InputStream entree;
    private OutputStream sortie;
    private String ipServeur;
    private int portServeur;

    public void connecter(String ip, int port) throws IOException {
        ipServeur = ip;
        portServeur = port;

        socket = new Socket(ipServeur, portServeur);
        socket.setSoTimeout(15000);
        entree = socket.getInputStream();
        sortie = socket.getOutputStream();
    }
    public synchronized void reconnecter() throws IOException {

        if (ipServeur == null || portServeur == 0) {
            throw new IOException("Adresse du serveur inconnue");
        }

        fermer();

        connecter(ipServeur, portServeur);
    }

    public synchronized String[] requete(String nom, String... params) throws IOException {
        String reponse = echanger(construire(nom, params));
        String[] champs = reponse.split("#", -1);

        if (champs.length < 2 || !champs[0].equals(nom)) {
            throw new ClientException("Réponse inattendue du serveur pour " + nom);
        }
        if (champs[1].equals("ko")) {
            throw new ClientException(champs.length > 2 ? champs[2] : "Erreur inconnue");
        }
        if (!champs[1].equals("ok")) {
            throw new ClientException("Statut inconnu dans la réponse à " + nom);
        }

        return Arrays.copyOfRange(champs, 2, champs.length);
    }

    public synchronized void envoyer(String nom, String... params) throws IOException {
        try {
            ecrireTrame(construire(nom, params));
        } catch (IOException e) {
            fermerSilencieusement();
            throw e;
        }
    }

    public synchronized String echanger(String requete) throws IOException {
        try {
            ecrireTrame(requete);

            byte[] enteteReponse = new byte[4];
            lireCompletement(entree, enteteReponse);

            String tailleTexte = new String(
                    enteteReponse, StandardCharsets.US_ASCII);

            int taille;
            try {
                taille = Integer.parseInt(tailleTexte);
            } catch (NumberFormatException e) {
                throw new IOException("En-tête de réponse invalide");
            }

            if (taille <= 0 || taille > 9999) {
                throw new IOException("Taille de réponse invalide");
            }

            byte[] reponse = new byte[taille];
            lireCompletement(entree, reponse);

            return new String(reponse, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Ne fermez pas systématiquement ici si vous voulez garder la session ouverte,
            // ou gérez la reconnexion automatique dans le contrôleur.
            throw e;
        }
    }

    private String construire(String nom, String... params) throws IOException {
        StringBuilder contenu = new StringBuilder(nom);

        for (String param : params) {
            if (param == null) {
                throw new ClientException("Champ manquant dans la requête " + nom);
            }
            if (param.contains("#")) {
                throw new ClientException("Le caractère '#' est interdit !");
            }
            contenu.append('#').append(param);
        }

        return contenu.toString();
    }

    private void ecrireTrame(String contenu) throws IOException {
        byte[] donnees = contenu.getBytes(StandardCharsets.UTF_8);

        if (donnees.length == 0 || donnees.length > 9999) {
            throw new IOException("Taille de requête invalide");
        }

        byte[] entete = String.format("%04d", donnees.length).getBytes(StandardCharsets.US_ASCII);
        byte[] trame = new byte[entete.length + donnees.length];
        System.arraycopy(entete, 0, trame, 0, entete.length);
        System.arraycopy(donnees, 0, trame, entete.length, donnees.length);

        sortie.write(trame);
        sortie.flush();
    }

    private void lireCompletement(
            InputStream entree, byte[] donnees) throws IOException {

        int total = 0;

        while (total < donnees.length) {
            int resultat = entree.read(
                    donnees, total, donnees.length - total
            );

            if (resultat == -1) {
                throw new IOException("Connexion fermée par le serveur");
            }

            total += resultat;
        }
    }

    private void fermerSilencieusement() {
        try {
            fermer();
        } catch (IOException ignored) {
        }
    }

    public synchronized void fermer() throws IOException {

        if (socket != null) {
            socket.close();
        }

        socket = null;
        entree = null;
        sortie = null;
    }
}