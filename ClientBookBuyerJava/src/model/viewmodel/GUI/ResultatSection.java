
package model.viewmodel.GUI;

import model.controller.ControllerActions;
import model.controller.ControllerBase;
import model.controller.ControllerResultat;
import model.viewmodel.ResultatVM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ResultatSection implements ResultatVM {

    private JPanel Book;
    private JTable tableBooks;
    private JScrollPane ListeLivre;
    private JButton buttonVider;
    private JButton buttonAnnuler;
    private JButton buttonAjouter;
    private JSlider sliderQte;
    private JLabel qteLabel;
    private JLabel stockLabel;

    private DefaultTableModel tableModel;

    public ResultatSection() {

        buttonAnnuler.setText(ControllerActions.AnnulerSession);
        buttonVider.setText(ControllerActions.ViderPanier);
        buttonAjouter.setText(ControllerActions.AjouterPanier);

        Book.setName("BookSection");

        stockLabel.setText("Quantite :");
        qteLabel.setText("0");

        sliderQte.setMinimum(0);
        sliderQte.setMaximum(1);
        sliderQte.setValue(0);

        sliderQte.addChangeListener(e -> {
            int quantite = sliderQte.getValue();
            qteLabel.setText("" + quantite);
        });

        initTable();
    }

    private void initTable() {

        String[] columns = {
                "ID", "Titre", "Auteur", "Year", "Price", "Stock"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableBooks.setModel(tableModel);

        // Masquer la colonne ID
        tableBooks.getColumnModel().getColumn(0).setMinWidth(0);
        tableBooks.getColumnModel().getColumn(0).setMaxWidth(0);
        tableBooks.getColumnModel().getColumn(0).setWidth(0);
    }

    public void clearTable() {
        if (tableModel != null) {
            tableModel.setRowCount(0);
        }
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JSlider getSliderQte() {
        return sliderQte;
    }

    public JPanel getMainPanel() {
        return Book;
    }

    @Override
    public void setControllerResultat(ControllerResultat c) {
        buttonAjouter.addActionListener(c);
        buttonVider.addActionListener(c);
        buttonAnnuler.addActionListener(c);
    }

    public void setEnabled(boolean b) {
        buttonAnnuler.setEnabled(b);
        buttonVider.setEnabled(b);
        buttonAjouter.setEnabled(b);
    }

    public JTable getTableBooks() {
        return tableBooks;
    }

    public void displayResults(String[] champs) {

        tableModel.setRowCount(0);

        int elementsParLigne = 6;

        for (int i = 0; i + elementsParLigne <= champs.length; i += elementsParLigne) {

            Object[] row = {
                    champs[i],       // ID
                    champs[i + 1],   // Titre
                    champs[i + 2],   // Auteur
                    champs[i + 3],   // Year
                    champs[i + 4],   // Price
                    champs[i + 5]    // Stock
            };

            tableModel.addRow(row);
        }
    }

    @Override
    public void setControllerBase(ControllerBase c) {

    }
}
