package model.viewmodel.GUI;

import model.controller.ControllerBase;
import model.viewmodel.BaseApp;

import javax.swing.*;

public class BaseSection implements BaseApp {
    private JPanel Base;
    private JLabel titreLabel;
    private JTextField titreText;
    private JTextField auteurText;
    private JLabel auteurLabel;
    private JLabel prixLabel;
    private JButton buttonRecherche;
    private JTextField sujetText;
    private JLabel sujetLabel;
    private JSpinner minPrixSpinner;
    private JSpinner maxPrixSpinner;
    private JLabel rechercheLabel;

    public BaseSection()
    {
        titreLabel.setText("Titre :");
        prixLabel.setText("Prix :");
        Base.setName("BaseSection");
    }
    public JPanel getMainPanel() {
        return Base;
    }

    public JTextField getTitreText() {
        return auteurText;
    }

    public void setEnabled(boolean b)
    {
        auteurText.setEnabled(b);
    }

    @Override
    public void setControllerBase(ControllerBase c) {
    }

    @Override
    public void clearAll() {
        auteurText.setText("");
    }

}
