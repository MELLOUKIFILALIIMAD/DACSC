package model.controller;

import model.dao.SubjectDAO;
import model.entity.Subject;
import model.viewmodel.GUI.MainView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ControllerSujet implements ActionListener {
    private SubjectDAO subjectDAO;
    private MainView mainView;

    public ControllerSujet(SubjectDAO subjectDAO, MainView mainView) {
        this.subjectDAO = subjectDAO;
        this.mainView = mainView;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}