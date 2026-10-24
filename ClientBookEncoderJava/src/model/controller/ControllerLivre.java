package model.controller;

import model.viewmodel.GUI.MainView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerLivre implements ActionListener {
    private MainView mainView;

    public ControllerLivre(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
