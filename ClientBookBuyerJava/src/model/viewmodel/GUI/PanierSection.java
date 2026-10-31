package model.viewmodel.GUI;

import model.controller.ControllerActions;
import model.controller.ControllerAuteur;
import model.controller.ControllerBase;
import model.viewmodel.AuthorSearchVM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.entity.Author;
import java.util.List;
public class PanierSection implements AuthorSearchVM {
    private JPanel Auteur;
    private JLabel totalLabel;
    private JTextField prixTotal;
    private JButton buttonPayer;
    private JLabel panierLabel;
    private JTable tablePanier;
    private DefaultTableModel tableModel;
    public PanierSection()
    {
        panierLabel.setText("Panier :");
        Auteur.setName("AuteurSection");
        initTable();
    }
    public JPanel getMainPanel() {
        return Auteur;
    }

    public void setEnabled(boolean b)
    {
    }

    private void initTable() {
        String[] columns = {"Livre", "Qté"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePanier.setModel(tableModel);
    }

    @Override
    public void setControllerAuteur(ControllerAuteur c) {

    }

    public void setControllerBase(ControllerBase c) {

    }
}
