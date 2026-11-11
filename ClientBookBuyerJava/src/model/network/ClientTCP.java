package model.network;

import model.protocol.Reponse;
import model.protocol.Requete;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class ClientTCP {

    private final String ipServeur;
    private final int portServeur;

    public ClientTCP(String ip, int port) {
        this.ipServeur = ip;
        this.portServeur = port;
    }

    public synchronized String[] requete(String nom, String... params) throws IOException {
        String[] champs = echanger(construire(nom, params)).getChamps();

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

    private Reponse echanger(String[] champs) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ipServeur, portServeur), 5000);
            socket.setSoTimeout(15000);

            ObjectOutputStream sortie = new ObjectOutputStream(socket.getOutputStream());
            sortie.flush();
            ObjectInputStream entree = new ObjectInputStream(socket.getInputStream());

            sortie.writeObject(new Requete(champs));
            sortie.flush();

            Object recu = entree.readObject();

            if (!(recu instanceof Reponse)) {
                throw new IOException("Réponse invalide du serveur");
            }

            return (Reponse) recu;

        } catch (ClassNotFoundException e) {
            throw new IOException("Réponse invalide du serveur", e);
        }
    }

    private String[] construire(String nom, String... params) throws IOException {
        String[] champs = new String[params.length + 1];
        champs[0] = nom;

        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                throw new ClientException("Champ manquant dans la requête " + nom);
            }
            champs[i + 1] = params[i];
        }

        return champs;
    }
}