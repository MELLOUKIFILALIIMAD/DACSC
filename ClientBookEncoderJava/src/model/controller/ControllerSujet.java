package model.controller;

import model.dao.SubjectDAO;
import model.entity.Subject;
import model.viewmodel.GUI.MainView;
import model.viewmodel.GUI.SubjectSection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ControllerSujet implements ActionListener {
    private SubjectSection subjectSection;
    private SubjectDAO subjectDAO;
    private MainView mainView;

    public ControllerSujet(SubjectSection subjectSection, SubjectDAO subjectDAO, MainView mainView) {
        this.subjectSection = subjectSection;
        this.subjectDAO = subjectDAO;
        this.mainView = mainView;

        this.subjectSection.setControllerSujet(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ControllerActions.AjouterSujet)) {
            String nom = subjectSection.promptForAddSubject();

            // Annulé ou vide : rien à faire
            if (nom == null || nom.trim().isEmpty()) {
                return;
            }

            // ***** ADD_SUBJECT#nom puis GET_SUBJECTS (mise à jour de la combobox) *****
            try {
                String message = subjectDAO.insert(new Subject(nom.trim()));
                mainView.showMessage(message);
            } catch (IOException ex) {
                mainView.showErrorMessage(ex.getMessage());
            }
        }
    }
}