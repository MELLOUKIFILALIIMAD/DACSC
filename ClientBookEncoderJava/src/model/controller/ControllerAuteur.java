package model.controller;

import model.dao.AuthorDAO;
import model.entity.Author;
import model.viewmodel.GUI.AuthorSection;
import model.viewmodel.GUI.MainView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;

public class ControllerAuteur implements ActionListener {
    private AuthorSection author;
    private AuthorDAO authorDAO;
    private MainView mainView;

    public ControllerAuteur(AuthorSection author, AuthorDAO authorDAO, MainView mainView) {
        this.author = author;
        this.authorDAO = authorDAO;
        this.mainView = mainView;

        this.author.setControllerAuteur(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ControllerActions.AjouterAuteur)) {
            String[] saisie = author.promptForAddAuthor();
            String nom = saisie[0];
            String prenom = saisie[1];
            String date = saisie[2];


            // Annulé : rien à faire
            if (nom == null || prenom == null) {
                return;
            }

            if (nom.trim().isEmpty() || prenom.trim().isEmpty()) {
                mainView.showErrorMessage("Veuillez entrer le nom et le prénom !");
                return;
            }
            else if (date.trim().isEmpty())
            {
                mainView.showErrorMessage("Veuillez entrer la date de naissance !");
                return;
            }

            LocalDate dateNaissance = LocalDate.parse(date);
            // ***** ADD_AUTHOR#nom#prenom puis GET_AUTHORS (mise à jour de la combobox) *****
            try {
                String message = authorDAO.insert(new Author(nom.trim(), prenom.trim(), dateNaissance));
                mainView.showMessage(message);
            } catch (IOException ex) {
                mainView.showErrorMessage(ex.getMessage());
            }
        }
    }
}