package model.controller;

import model.viewmodel.GUI.SubjectSection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerSujet implements ActionListener {
    private SubjectSection subjectSection;

    public ControllerSujet(SubjectSection subjectSection) {
        this.subjectSection = subjectSection;

        this.subjectSection.setControllerSujet(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ControllerActions.AjouterSujet)) {
            subjectSection.promptForAddSubject();
        }
    }
}
