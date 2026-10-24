package model.viewmodel.GUI;

import model.controller.ControllerActions;
import model.controller.ControllerAuteur;
import model.controller.ControllerBase;
import model.viewmodel.AuthorSearchVM;

import javax.swing.*;
import java.awt.*;
import model.entity.Author;
import java.util.List;
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
    // Remplit la combobox avec les auteurs reçus du serveur
    public void setAuthors(List<Author> authors) {
        auteurCmb.removeAllItems();
        for (Author a : authors) {
            auteurCmb.addItem(a);
        }
    }

    public Author getSelectedAuthor() {
        return (Author) auteurCmb.getSelectedItem();
    }

    @Override
    public String[] promptForAddAuthor() {
        Window parent = SwingUtilities.getWindowAncestor(Auteur);

        JDialogAdd dialog = new JDialogAdd(parent, true);
        dialog.setVisible(true);

        String Nom = dialog.getNom();
        String Prenom = dialog.getPrenom();
        String DateNaissance = dialog.getDateNaissance();

        return new String[]{Nom, Prenom, DateNaissance};
    }
}
