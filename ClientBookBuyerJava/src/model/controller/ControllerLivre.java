package model.controller;

import model.entity.Caddy;
import model.viewmodel.GUI.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerLivre implements ActionListener {
    private MainView mainView;
    private Caddy caddy;
    private ResultatSection bookSection;
    private PanierSection panierSection;
    private BaseSection baseSection;

    public ControllerLivre(MainView mainView, Caddy caddy, ResultatSection bookSection, PanierSection panierSection, BaseSection baseSection) {
        this.mainView = mainView;
        this.caddy = caddy;
        this.bookSection = bookSection;
        this.panierSection = panierSection;
        this.baseSection = baseSection;

        this.mainView.setControllerLivre(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}