package model.viewmodel.GUI;

import model.controller.ControllerActions;
import model.controller.ControllerBase;
import model.viewmodel.BaseApp;

import javax.swing.*;

public class BaseSection implements BaseApp {
    private JPanel Base;
    private JLabel titreLabel;
    private JLabel ISBNLabel;
    private JTextField titreText;
    private JTextField ISBNTexte;
    private JButton ViderBouton;
    private JLabel pagesLabel;
    private JLabel prixLabel;
    private JLabel anneeLabel;
    private JLabel stockLabel;
    private JSpinner pagesSpinner;
    private JSpinner anneeSpinner;
    private JSpinner stockSpinner;
    private JSpinner prixSpinner;

    public BaseSection()
    {
        titreLabel.setText("Titre :");
        ISBNLabel.setText("ISBN :");
        pagesLabel.setText("Pages :");
        prixLabel.setText("Prix :");
        anneeLabel.setText("Année de publication :");
        stockLabel.setText("Stock :");
        pagesSpinner.setModel(new SpinnerNumberModel(0,0,999,1));
        anneeSpinner.setModel(new SpinnerNumberModel(1900,0,9999,1));
        stockSpinner.setModel(new SpinnerNumberModel(0,0,999,1));
        SpinnerModel spinnerModel = new SpinnerNumberModel(Float.valueOf(0.00f),Float.valueOf(0.00f),Float.valueOf(999.00f),Float.valueOf(10.00f));
        prixSpinner.setModel(spinnerModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(prixSpinner, "0.00");
        prixSpinner.setEditor(editor);
        Base.setName("BaseSection");

        ViderBouton.setText(ControllerActions.ViderChamps);
    }
    public JPanel getMainPanel() {
        return Base;
    }

    public JTextField getTitreText() {
        return titreText;
    }

    public JTextField getISBNText() {
        return ISBNTexte;
    }

    public JSpinner getPagesSpinner() {
        return pagesSpinner;
    }

    public JSpinner getAnneeSpinner() {
        return anneeSpinner;
    }

    public JSpinner getStockSpinner() {
        return stockSpinner;
    }

    public JSpinner getPrixSpinner() {
        return prixSpinner;
    }

    public void setEnabled(boolean b)
    {
        ViderBouton.setEnabled(b);
        titreText.setEnabled(b);
        ISBNTexte.setEnabled(b);
        pagesSpinner.setEnabled(b);
        anneeSpinner.setEnabled(b);
        stockSpinner.setEnabled(b);
        prixSpinner.setEnabled(b);
    }

    @Override
    public void setControllerBase(ControllerBase c) {
        ViderBouton.addActionListener(c);
    }

    @Override
    public void clearAll() {
        titreText.setText("");
        ISBNTexte.setText("");
        pagesSpinner.setValue(0);
        prixSpinner.setValue(0.00f);
        anneeSpinner.setValue(1900);
        stockSpinner.setValue(0);
    }


}