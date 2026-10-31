package model.controller;

import model.dao.AuthorDAO;
import model.dao.SubjectDAO;
import model.dao.UserDAO;
import model.entity.CaddyItem;
import model.entity.Client;
import model.viewmodel.GUI.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class ControllerBase implements ActionListener {
    private BaseSection baseSection;
    private MainView mainView;
    private AuthorSection authorSection;
    private BookSection bookSection;
    private SubjectSection subjectSection;

    private UserDAO userDAO;
    private AuthorDAO authorDAO;
    private SubjectDAO subjectDAO;

    private String loginCourant = null;

    public void run() {
        mainView.run();
    }

    public ControllerBase(BaseSection baseSection, MainView mainView, AuthorSection authorSection,
                          BookSection bookSection, SubjectSection subjectSection,
                          UserDAO userDAO, AuthorDAO authorDAO, SubjectDAO subjectDAO) {
        this.baseSection = baseSection;
        this.mainView = mainView;
        this.authorSection = authorSection;
        this.bookSection = bookSection;
        this.subjectSection = subjectSection;

        this.userDAO = userDAO;
        this.authorDAO = authorDAO;
        this.subjectDAO = subjectDAO;

        this.subjectSection.setControllerBase(this);
        this.baseSection.setControllerBase(this);
        this.mainView.setControllerBase(this);
        this.authorSection.setControllerBase(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ControllerActions.ViderChamps)) {
            baseSection.clearAll();
        }

        if (e.getActionCommand().equals(ControllerActions.Login)) {
            String[] u = mainView.promptForLogin();
            String login = u[0];
            String password = u[1];

            if (login == null || password == null) {
            }
            else
            {
                if (login.isEmpty() || password.isEmpty()) {
                    mainView.showErrorMessage("Veuillez entrer le login ou le mot de passe !");
                    return;
                }

                // ***** LOGIN#login#password *****
                try {
                    userDAO.login(login, password);
                } catch (IOException ex) {
                    mainView.showErrorMessage("Erreur de connexion : " + ex.getMessage());
                    return;
                }
                loginCourant = login;

                // ***** GET_AUTHORS puis GET_SUBJECTS : remplissent les combobox *****
                try {
                    authorSection.setAuthors(authorDAO.findAll());
                    subjectSection.setSubjects(subjectDAO.findAll());
                } catch (IOException ex) {
                    mainView.showErrorMessage("Erreur lors de la récupération des données : " + ex.getMessage());

                    // On se déconnecte, sinon le serveur répondrait "déjà connecté" au prochain essai
                    try {
                        userDAO.logout(login);
                    } catch (IOException ignored) {
                    }
                    loginCourant = null;
                    return;
                }

                mainView.setEnabled(true);
                baseSection.setEnabled(true);
                authorSection.setEnabled(true);
                subjectSection.setEnabled(true);
            }
        }

        if (e.getActionCommand().equals(ControllerActions.Logout)) {
            // ***** LOGOUT#login (pas de réponse du serveur) *****
            if (loginCourant != null) {
                try {
                    userDAO.logout(loginCourant);
                } catch (IOException ex) {
                    mainView.showErrorMessage("Erreur lors de la déconnexion : " + ex.getMessage());
                }
                loginCourant = null;
            }

            authorSection.setAuthors(new ArrayList<CaddyItem>());
            subjectSection.setSubjects(new ArrayList<Client>());

            mainView.setEnabled(false);
            baseSection.setEnabled(false);
            authorSection.setEnabled(false);
            subjectSection.setEnabled(false);
            bookSection.clearTable();
        }
    }
}