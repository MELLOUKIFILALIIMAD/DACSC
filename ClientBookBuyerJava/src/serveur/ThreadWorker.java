package serveur;


import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadWorker extends Thread {

    // Le serveur ne désérialise QUE nos classes et quelques classes Java.
    // Sans ça, un client malveillant pourrait envoyer n'importe quel objet.
    private static final String FILTRE =
            "bspp.*;model.entity.*;model.viewmodel.*;java.lang.*;java.util.*;java.time.*;!*";

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
                socket = file.retirer();        // bloque tant que la file est vide
            } catch (InterruptedException e) {
                return;
            }
            traiterRequete(socket);
        }
    }

    private void traiterRequete(Socket socket) {
        try (Socket s = socket) {
            s.setSoTimeout(5000);               // un client muet ne bloque pas le thread indéfiniment

            ObjectOutputStream sortie = new ObjectOutputStream(s.getOutputStream());
            sortie.flush();
            ObjectInputStream entree = new ObjectInputStream(s.getInputStream());
            entree.setObjectInputFilter(ObjectInputFilter.Config.createFilter(FILTRE));

            Object recu = entree.readObject();
            System.out.println("(" + getName() + ") Requête reçue : " + recu.getClass().getSimpleName());


            sortie.flush();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("(" + getName() + ") Requête abandonnée : " + e);
        }
    }
}