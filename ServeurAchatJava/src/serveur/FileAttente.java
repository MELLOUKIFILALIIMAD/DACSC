package serveur;

import java.net.Socket;
import java.util.LinkedList;

public class FileAttente {

    private final LinkedList<Socket> sockets = new LinkedList<>();

    public synchronized void ajouter(Socket socket) {
        sockets.addLast(socket);
        notify();                       // réveille UN thread du pool
    }

    public synchronized Socket retirer() throws InterruptedException {
        while (sockets.isEmpty()) {     // tant qu'il n'y a rien à faire, on dort
            wait();
        }
        return sockets.removeFirst();
    }
}