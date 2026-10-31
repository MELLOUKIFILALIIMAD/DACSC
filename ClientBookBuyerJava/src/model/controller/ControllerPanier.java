package model.controller;

import model.viewmodel.GUI.PanierSection;
import model.viewmodel.GUI.MainView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerPanier implements ActionListener {
    private PanierSection panier;

    public ControllerPanier(PanierSection panier) {
        this.panier = panier;

        this.panier.setControllerPanier(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}