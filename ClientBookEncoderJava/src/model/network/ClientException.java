package model.network;

import java.io.IOException;
import java.io.Serial;

// Réponse "ko" du serveur ou réponse invalide (la raison est dans getMessage())
public class ClientException extends IOException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ClientException(String message) {
        super(message);
    }
}