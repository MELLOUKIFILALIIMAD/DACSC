
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

        stockLabel.setText("Quantité :");
        qteLabel.setText("0");

        sliderQte.setMinimum(0);
        sliderQte.setMaximum(0);
        sliderQte.setValue(0);
        sliderQte.setEnabled(false);

        sliderQte.addChangeListener(e -> {
            int quantite = sliderQte.getValue();
            qteLabel.setText(String.valueOf(quantite));
        });

        initTable();

        tableBooks.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                int ligne = tableBooks.getSelectedRow();

                if (ligne >= 0) {
                    int stock = Integer.parseInt(
                            tableModel.getValueAt(ligne, 5).toString()
                    );

                    sliderQte.setMinimum(0);
                    sliderQte.setMaximum(stock);
                    sliderQte.setValue(0);
                    sliderQte.setEnabled(stock > 0);

                } else {
                    sliderQte.setMaximum(0);
                    sliderQte.setValue(0);
                    sliderQte.setEnabled(false);
                }

                qteLabel.setText("0");
            }
        });
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

        tableBooks.getColumnModel().getColumn(0).setMinWidth(0);
        tableBooks.getColumnModel().getColumn(0).setMaxWidth(0);
        tableBooks.getColumnModel().getColumn(0).setWidth(0);
    }

    public void clearTable() {
        if (tableModel != null) {
            tableModel.setRowCount(0);
        }

        sliderQte.setMaximum(0);
        sliderQte.setValue(0);
        sliderQte.setEnabled(false);
        qteLabel.setText("0");
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

        sliderQte.setMaximum(0);
        sliderQte.setValue(0);
        sliderQte.setEnabled(false);
        qteLabel.setText("0");

        int elementsParLigne = 6;

        for (int i = 0; i + elementsParLigne <= champs.length; i += elementsParLigne) {

            Object[] row = {
                    champs[i],
                    champs[i + 1],
                    champs[i + 2],
                    champs[i + 3],
                    champs[i + 4],
                    champs[i + 5]
            };

            tableModel.addRow(row);
        }
    }

    @Override
    public void setControllerBase(ControllerBase c) {

    }
}
