package model.viewmodel.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ResultatSection {
    private JPanel Book;
    private JTable tableBooks;
    private JScrollPane ListeLivre;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private DefaultTableModel tableModel;

    public ResultatSection() {
        Book.setName("BookSection");
        initTable();
    }
    private void initTable() {
        String[] columns = {"Title", "CaddyItem", "Client", "Year", "Price", "Stock"};
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
