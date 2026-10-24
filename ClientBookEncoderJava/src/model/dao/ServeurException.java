package model.dao;

import java.io.IOException;

// Réponse "ko" du serveur ou réponse invalide (la raison est dans getMessage())
public class ServeurException extends IOException {
    private static final long serialVersionUID = 1L;

    public ServeurException(String message) {
        super(message);
    }
}