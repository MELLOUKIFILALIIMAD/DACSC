package model.controller;

import model.viewmodel.GUI.PanierSection;
import model.viewmodel.GUI.MainView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerAuteur implements ActionListener {
    private PanierSection author;
    private MainView mainView;

    public ControllerAuteur(PanierSection author, MainView mainView) {
        this.author = author;
        this.mainView = mainView;

        this.author.setControllerAuteur(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}