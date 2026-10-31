package model.controller;

import model.viewmodel.GUI.BaseSection;
import model.viewmodel.GUI.MainView;
import model.viewmodel.GUI.PanierSection;
import model.viewmodel.GUI.ResultatSection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerBase implements ActionListener {
    private MainView mainView;
    private PanierSection panierSection;
    private ResultatSection resultatSection;
    private BaseSection baseSection;

    public ControllerBase(MainView mainView, PanierSection panierSection, ResultatSection resultatSection, BaseSection baseSection) {
        this.mainView = mainView;
        this.panierSection = panierSection;
        this.resultatSection = resultatSection;
        this.baseSection = baseSection;

        this.baseSection.setControllerBase(this);
        this.panierSection.setControllerBase(this);
        this.resultatSection.setControllerBase(this);
        this.mainView.setControllerBase(this);
    }

    public void run() {
        mainView.run();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ControllerActions.Login)) {
            String[] u = mainView.promptForConnect();
            String Nom = u[0];
            String Prenom = u[1];

            // Traitement client

            mainView.setEnabled(true);
            panierSection.setEnabled(true);
            baseSection.setEnabled(true);
            resultatSection.setEnabled(true);
        }

        if (e.getActionCommand().equals(ControllerActions.Logout)) {

            mainView.setEnabled(false);
            panierSection.setEnabled(false);
            baseSection.setEnabled(false);
            resultatSection.setEnabled(false);
        }
    }
}
