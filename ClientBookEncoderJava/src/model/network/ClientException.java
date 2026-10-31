package model.network;

import java.io.IOException;

// Réponse "ko" du serveur ou réponse invalide (la raison est dans getMessage())
public class ClientException extends IOException {
    private static final long serialVersionUID = 1L;

    public ClientException(String message) {
        super(message);
    }
}