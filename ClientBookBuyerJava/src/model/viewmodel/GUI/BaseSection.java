package model.viewmodel.GUI;

import model.controller.ControllerActions;
import model.controller.ControllerBase;
import model.viewmodel.BaseVM;

import javax.swing.*;
import java.util.Map;

public class BaseSection implements BaseVM {
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
        minPrixSpinner.setModel(new SpinnerNumberModel(0.0, 0.0, Integer.MAX_VALUE, 0.5));
        maxPrixSpinner.setModel(new SpinnerNumberModel(0.0, 0.0, Integer.MAX_VALUE, 0.5));
    }
    public JPanel getMainPanel() {
        return Base;
    }

    public JTextField getTitreText() {
        return titreText;
    }
    public JTextField getAuteurText() { return auteurText;}
    public JTextField getSujetText() {return sujetText;}
    public JSpinner getMaxPrixSpinner() {return maxPrixSpinner;}

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
        buttonRecherche.addActionListener(c);
    }

    @Override
    public void clearAll() {
        titreText.setText("");
        auteurText.setText("");
        sujetText.setText("");
        maxPrixSpinner.setValue(1000);
        minPrixSpinner.setValue(0);
    }
    public void viderChamps() {
        titreText.setText("");
        auteurText.setText("");
        sujetText.setText("");
        maxPrixSpinner.setValue(0);
    }

    public JSpinner getMinPrixSpinner() {
        return minPrixSpinner;
    }
}
