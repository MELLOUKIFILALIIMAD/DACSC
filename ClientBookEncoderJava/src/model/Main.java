package model;

import model.controller.ControllerAuteur;
import model.controller.ControllerBase;
import model.controller.ControllerSujet;
import model.dao.AuthorDAO;
import model.dao.ServeurTCP;
import model.dao.SubjectDAO;
import model.dao.UserDAO;
import model.viewmodel.GUI.*;
import javax.swing.*;
import com.formdev.flatlaf.FlatIntelliJLaf;

import java.io.IOException;

public class Main {

    // IP de la machine du serveur Encodage et PORT_ENCODING (voir configuration.txt côté serveur)
    private static final String IP_SERVEUR = "192.168.239.131";
    private static final int PORT_ENCODING = 5000;

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();

        // ***** Connexion unique au serveur (créée UNE seule fois pour toute l'application) *****
        ServeurTCP serveur = new ServeurTCP();

        try {
            serveur.connecter(IP_SERVEUR, PORT_ENCODING);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Impossible de joindre le serveur " + IP_SERVEUR + ":" + PORT_ENCODING
                            + "\n" + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ***** DAO : tous partagent la même connexion *****
        final UserDAO userDAO = new UserDAO(serveur);
        final AuthorDAO authorDAO = new AuthorDAO(serveur);
        final SubjectDAO subjectDAO = new SubjectDAO(serveur);

        // ***** Interface Swing (lancée dans le thread graphique) *****
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AuthorSection authorSection = new AuthorSection();
                BookSection bookSection = new BookSection();
                SubjectSection subjectSection = new SubjectSection();
                BaseSection baseSection = new BaseSection();
                MainView mainView = new MainView(baseSection, authorSection, bookSection, subjectSection);

                mainView.setEnabled(false);
                baseSection.setEnabled(false);
                authorSection.setEnabled(false);
                subjectSection.setEnabled(false);

                ControllerBase controllerBase = new ControllerBase(baseSection, mainView, authorSection,
                        bookSection, subjectSection, userDAO, authorDAO, subjectDAO);

                new ControllerSujet(subjectSection, subjectDAO, mainView);

                new ControllerAuteur(authorSection, authorDAO, mainView);

                controllerBase.run();
            }
        });
    }
}