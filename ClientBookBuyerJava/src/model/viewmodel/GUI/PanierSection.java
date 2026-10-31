package model.viewmodel.GUI;

import model.controller.ControllerActions;
import model.controller.ControllerPanier;
import model.controller.ControllerBase;
import model.viewmodel.PanierVM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanierSection implements PanierVM {
    private JPanel Panier;
    private JLabel totalLabel;
    private JTextField prixTotal;
    private JButton buttonPayer;
    private JLabel panierLabel;
    private JTable tablePanier;
    private JLabel euroLabel;
    private DefaultTableModel tableModel;
    public PanierSection()
    {
        panierLabel.setText("Panier :");
        totalLabel.setText("Total :");
        euroLabel.setText("€");
        prixTotal.setText("0");
        prixTotal.setEnabled(false);
        prixTotal.setBorder(null);
        buttonPayer.setText(ControllerActions.ValiderPanier);
        tableModel = (DefaultTableModel) tablePanier.getModel();
        Panier.setName("Panier");
        initTable();
    }
    public JPanel getMainPanel() {
        return Panier;
    }

    public void setEnabled(boolean b)
    {
        buttonPayer.setEnabled(b);
        tablePanier.setEnabled(b);
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

    public JTable getTable()
    {
        return tablePanier;
    }

    public JTextField getPrixTotal()
    {
        return prixTotal;
    }

    @Override
    public void setControllerPanier(ControllerPanier c) {
        buttonPayer.addActionListener(c);
    }

    public void setControllerBase(ControllerBase c) {

    }
}
