package model.viewmodel.GUI;

import model.controller.ControllerLivre;
import model.controller.ControllerSujet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BookSection {
    private JPanel Book;
    private JTable tableBooks;
    private JScrollPane ListeLivre;
    private DefaultTableModel tableModel;

    public BookSection() {
        Book.setName("BookSection");
        initTable();
    }
    private void initTable() {
        String[] columns = {"ID", "Title", "ISBN", "Author", "Subject", "Year", "Price", "Stock"};
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

    public JPanel getMainPanel() {
        return Book;
    }
}