package model.viewmodel.GUI;

import model.controller.ControllerActions;
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
    private JLabel maxLabel;
    private JLabel minLabel;

    public BaseSection()
    {
        titreLabel.setText("Titre :");
        prixLabel.setText("Prix :");
        sujetLabel.setText("Sujet :");
        auteurLabel.setText("Auteur :");
        buttonRecherche.setText(ControllerActions.Rechercher);
        minLabel.setText("Min :");
        maxLabel.setText("Max :");
        rechercheLabel.setText("Recherche");
        minPrixSpinner.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        maxPrixSpinner.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
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
        minPrixSpinner.setEnabled(b);
        maxPrixSpinner.setEnabled(b);
        titreText.setEnabled(b);
        sujetText.setEnabled(b);
        buttonRecherche.setEnabled(b);
    }

    @Override
    public void setControllerBase(ControllerBase c) {
    }

    @Override
    public void clearAll() {
        auteurText.setText("");
    }

}
