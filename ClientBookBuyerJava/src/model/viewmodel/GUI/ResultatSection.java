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
        String[] columns = {"Titre", "Auteur", "Year", "Price", "Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBooks.setModel(tableModel);
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

    @Override
    public void setControllerBase(ControllerBase c)
    {

    }
}
