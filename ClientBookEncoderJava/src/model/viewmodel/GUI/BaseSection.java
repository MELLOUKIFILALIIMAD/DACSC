package model.viewmodel.GUI;

import javax.swing.*;

public class BaseSection {
    private JPanel Base;
    private JLabel titreLabel;
    private JLabel ISBNLabel;
    private JTextField textField1;
    private JTextField textField2;
    private JButton button1;
    private JLabel pagesLabel;
    private JLabel prixLabel;
    private JLabel anneeLabel;
    private JLabel stockLabel;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JSpinner spinner3;
    private JSpinner spinner4;

    public BaseSection()
    {
        Base.setName("BaseSection");
    }
    public JPanel getMainPanel() {
        return Base;
    }
}
