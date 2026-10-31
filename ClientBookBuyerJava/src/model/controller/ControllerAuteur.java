package model.controller;

import model.dao.AuthorDAO;
import model.viewmodel.GUI.PanierSection;
import model.viewmodel.GUI.MainView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerAuteur implements ActionListener {
    private PanierSection author;
    private AuthorDAO authorDAO;
    private MainView mainView;

    public ControllerAuteur(PanierSection author, AuthorDAO authorDAO, MainView mainView) {
        this.author = author;
        this.authorDAO = authorDAO;
        this.mainView = mainView;

        this.author.setControllerAuteur(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}