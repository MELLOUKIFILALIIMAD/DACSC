package model.viewmodel.GUI;

import model.controller.ControllerActions;
import model.controller.ControllerBase;
import model.controller.ControllerSujet;
import model.viewmodel.SubjectSearchVM;

import javax.swing.*;
import java.awt.*;

public class SubjectSection implements SubjectSearchVM {
    private JPanel Sujet;
    private JComboBox sujetCmb;
    private JButton AjouterBouton;
    private JLabel SubjectLabel;

    public SubjectSection()
    {
        SubjectLabel.setText("Sujet :");
        Sujet.setName("SubjectSection");
        AjouterBouton.setText(ControllerActions.AjouterSujet);
    }
    public JPanel getMainPanel() {
        return Sujet;
    }

    public JButton getAjouterBouton() {
        return AjouterBouton;
    }

    public void setEnabled(boolean b)
    {
        AjouterBouton.setEnabled(b);
        sujetCmb.setEnabled(b);
    }

    @Override
    public void setControllerSujet(ControllerSujet c) {
        AjouterBouton.addActionListener(c);
    }

    public void setControllerBase(ControllerBase c) {
        AjouterBouton.addActionListener(c);
    }

    @Override
    public String promptForAddSubject() {
        Window parent = SwingUtilities.getWindowAncestor(Sujet);

        JDialogAdd dialog = new JDialogAdd(parent, false);
        dialog.setVisible(true);
        String Nom = dialog.getNom();

        return Nom;
    }
}
