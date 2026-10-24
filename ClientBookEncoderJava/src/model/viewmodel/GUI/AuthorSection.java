package model.viewmodel.GUI;

import model.controller.ControllerActions;
import model.controller.ControllerAuteur;
import model.controller.ControllerBase;
import model.viewmodel.AuthorSearchVM;

import javax.swing.*;
import java.awt.*;

public class AuthorSection implements AuthorSearchVM {
    private JPanel Auteur;
    private JComboBox auteurCmb;
    private JLabel AuteurLabel;
    private JButton AjouterBouton;

    public AuthorSection()
    {
        AuteurLabel.setText("Auteur :");
        Auteur.setName("AuteurSection");
        AjouterBouton.setText(ControllerActions.AjouterAuteur);
    }
    public JPanel getMainPanel() {
        return Auteur;
    }

    public JButton getAjouterBouton() {
        return AjouterBouton;
    }

    public void setEnabled(boolean b)
    {
        AjouterBouton.setEnabled(b);
        auteurCmb.setEnabled(b);
    }

    @Override
    public void setControllerAuteur(ControllerAuteur c) {
        AjouterBouton.addActionListener(c);
    }

    public void setControllerBase(ControllerBase c) {
        AjouterBouton.addActionListener(c);
    }

    @Override
    public String[] promptForAddAuthor() {
        Window parent = SwingUtilities.getWindowAncestor(Auteur);

        JDialogAdd dialog = new JDialogAdd(parent, true);
        dialog.setVisible(true);
        String Nom = dialog.getNom();
        String Prenom =  dialog.getPrenom();

        return new String[]{Nom,Prenom};
    }

}
