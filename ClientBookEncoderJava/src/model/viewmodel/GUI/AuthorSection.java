package model.viewmodel.GUI;

import javax.swing.*;

public class AuthorSection {
    private JPanel Auteur;
    private JComboBox comboBox1;
    private JLabel AuteurLabel;
    private JButton button1;

    public AuthorSection()
    {
        Auteur.setName("AuteurSection");
    }
    public JPanel getMainPanel() {
        return Auteur;
    }
}
