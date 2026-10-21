package model.viewmodel.GUI;

import javax.swing.*;

public class SubjectSection {
    private JPanel Sujet;
    private JComboBox comboBox1;
    private JButton button1;
    private JLabel SubjectLabel;
    private JLabel AuteurLabel;

    public SubjectSection()
    {
        Sujet.setName("SubjectSection");
    }
    public JPanel getMainPanel() {
        return Sujet;
    }
}
