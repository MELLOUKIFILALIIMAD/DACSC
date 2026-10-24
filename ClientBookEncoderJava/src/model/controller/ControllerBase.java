package model.controller;

import model.viewmodel.GUI.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerBase implements ActionListener {
    private BaseSection baseSection;
    private MainView mainView;
    private AuthorSection authorSection;
    private BookSection bookSection;
    private SubjectSection subjectSection;

    public void run() {
        mainView.run();
    }

    public ControllerBase(BaseSection baseSection, MainView mainView, AuthorSection authorSection, BookSection bookSection, SubjectSection subjectSection ) {
        this.baseSection = baseSection;
        this.mainView = mainView;
        this.authorSection = authorSection;
        this.bookSection = bookSection;
        this.subjectSection = subjectSection;

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
                // traitement login & password

                mainView.setEnabled(true);
                baseSection.setEnabled(true);
                authorSection.setEnabled(true);
                subjectSection.setEnabled(true);
            }
        }

        if (e.getActionCommand().equals(ControllerActions.Logout)) {
            mainView.setEnabled(false);
            baseSection.setEnabled(false);
            authorSection.setEnabled(false);
            subjectSection.setEnabled(false);
        }
    }
}
