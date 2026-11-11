package model.viewmodel.GUI;

import model.controller.ControllerActions;
import model.controller.ControllerPanier;
import model.controller.ControllerBase;
import model.viewmodel.PanierVM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanierSection implements PanierVM {

    private JPanel Panier;
    private JLabel totalLabel;
    private JTextField prixTotal;
    private JButton buttonPayer;
    private JLabel panierLabel;
    private JTable tablePanier;
    private JLabel euroLabel;
    private DefaultTableModel tableModel;

    private ControllerBase controllerBase;

    public PanierSection() {
        panierLabel.setText("Panier :");
        totalLabel.setText("Total :");
        euroLabel.setText("€");
        prixTotal.setText("0");
        prixTotal.setEnabled(false);
        prixTotal.setBorder(null);

        buttonPayer.setText(ControllerActions.ValiderPanier);

        Panier.setName("Panier");

        initTable();
        initMenuContextuel();
    }

    public JPanel getMainPanel() {
        return Panier;
    }

    public void setEnabled(boolean b) {
        buttonPayer.setEnabled(b);
        tablePanier.setEnabled(b);
    }

    private void initTable() {
        String[] columns = {"ID", "Livre", "Qté"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablePanier.setModel(tableModel);

        // On masque la colonne ID, mais on la conserve dans le modèle.
        tablePanier.removeColumn(tablePanier.getColumnModel().getColumn(0));
    }

    private void initMenuContextuel() {

        final JPopupMenu menu = new JPopupMenu();

        JMenuItem supprimerItem = new JMenuItem("Supprimer ce livre");
        menu.add(supprimerItem);

        supprimerItem.addActionListener(e -> {

            int ligneSelectionnee = tablePanier.getSelectedRow();

            if (ligneSelectionnee == -1) {
                return;
            }

            int ligneModele = tablePanier.convertRowIndexToModel(ligneSelectionnee);

            String bookId = tableModel.getValueAt(ligneModele, 0).toString();
            String titre = tableModel.getValueAt(ligneModele, 1).toString();
            int quantiteMax = Integer.parseInt(
                    tableModel.getValueAt(ligneModele, 2).toString()
            );

            // Création du spinner entre 1 et la quantité présente dans le panier.
            SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
                    1,
                    1,
                    quantiteMax,
                    1
            );

            JSpinner spinner = new JSpinner(spinnerModel);

            JPanel panel = new JPanel(new BorderLayout(5, 5));

            panel.add(new JLabel(
                    "Combien de livres voulez-vous supprimer de \"" + titre + "\" ?"
            ), BorderLayout.NORTH);

            panel.add(spinner, BorderLayout.CENTER);

            int choix = JOptionPane.showConfirmDialog(
                    Panier,
                    panel,
                    "Supprimer un livre",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choix == JOptionPane.OK_OPTION) {

                int quantite = (Integer) spinner.getValue();

                if (controllerBase != null) {
                    controllerBase.supprimerLivreDuPanier(
                            bookId,
                            String.valueOf(quantite)
                    );
                }
            }
        });

        tablePanier.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                afficherMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                afficherMenu(e);
            }

            private void afficherMenu(MouseEvent e) {

                if (e.isPopupTrigger()) {

                    int ligne = tablePanier.rowAtPoint(e.getPoint());

                    if (ligne >= 0) {
                        tablePanier.setRowSelectionInterval(ligne, ligne);
                        menu.show(tablePanier, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    public JTable getTable() {
        return tablePanier;
    }

    public JTextField getPrixTotal() {
        return prixTotal;
    }

    @Override
    public void setControllerPanier(ControllerPanier c) {
        buttonPayer.addActionListener(c);
    }

    public void afficherPanier(String[] champs) {
        tableModel.setRowCount(0);

        if (champs.length == 0) {
            prixTotal.setText("0.00");
            return;
        }

        double total = Double.parseDouble(champs[0]);

        for (int i = 1; i + 3 < champs.length; i += 4) {

            String bookId = champs[i];
            String titre = champs[i + 1];
            String quantite = champs[i + 2];

            tableModel.addRow(new Object[]{
                    bookId,
                    titre,
                    quantite
            });
        }

        prixTotal.setText(String.format("%.2f", total));
    }

    public void setControllerBase(ControllerBase c) {
        this.controllerBase = c;
    }
}