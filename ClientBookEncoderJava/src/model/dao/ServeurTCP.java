
package model.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServeurTCP {

    private Socket socket;
    private InputStream entree;
    private OutputStream sortie;

    public void connecter(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        entree = socket.getInputStream();
        sortie = socket.getOutputStream();
    }

    public synchronized String echanger(String requete) throws IOException {
        byte[] donnees = requete.getBytes(StandardCharsets.UTF_8);

        if (donnees.length == 0 || donnees.length > 9999) {
            throw new IOException("Taille de requête invalide");
        }

        String entete = String.format("%04d", donnees.length);
        sortie.write(entete.getBytes(StandardCharsets.US_ASCII));
        sortie.write(donnees);
        sortie.flush();

        byte[] enteteReponse = new byte[4];
        lireCompletement(entree, enteteReponse);

        String tailleTexte = new String(
                enteteReponse, StandardCharsets.US_ASCII);

        int taille = Integer.parseInt(tailleTexte);

        if (taille <= 0 || taille > 9999) {
            throw new IOException("Taille de réponse invalide");
        }

        byte[] reponse = new byte[taille];
        lireCompletement(entree, reponse);

        return new String(reponse, StandardCharsets.UTF_8);
    }

    private void lireCompletement(
            InputStream entree, byte[] donnees) throws IOException {

        int total = 0;

        while (total < donnees.length) {
            int resultat = entree.read(
                    donnees, total, donnees.length - total);

            if (resultat == -1) {
                throw new IOException("Connexion fermée par le serveur");
            }

            total += resultat;
        }
    }

    public void fermer() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }
}
