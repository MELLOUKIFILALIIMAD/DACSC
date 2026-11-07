
package model.controller;

import model.dao.CaddyDAO;
import model.entity.Caddy;
import model.entity.Client;
import model.viewmodel.GUI.PanierSection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ControllerPanier implements ActionListener {

    private PanierSection panier;
    private ControllerBase controllerBase;
    private CaddyDAO caddyDAO;

    public ControllerPanier(PanierSection panier,
                            ControllerBase controllerBase,
                            CaddyDAO caddyDAO) {

        this.panier = panier;
        this.controllerBase = controllerBase;
        this.caddyDAO = caddyDAO;

        this.panier.setControllerPanier(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals(ControllerActions.ValiderPanier)) {

            Client currentClient = controllerBase.getLoggedInClient();

            if (currentClient == null) {

                JOptionPane.showMessageDialog(
                        null,
                        "Aucun client connecté !",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            try {

                Caddy openCaddy =
                        caddyDAO.findOpenByClient(currentClient.getId());

                if (openCaddy == null) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Votre panier est vide ou inexistant.",
                            "Information",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    return;
                }

                // Vérifier le stock, diminuer les quantités
                // et valider le paiement
                boolean success =
                        caddyDAO.validerPanier(openCaddy.getId());

                if (success) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Paiement validé avec succès ! Merci pour votre achat.",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    // Vider l'affichage du panier
                    panier.getPrixTotal().setText("0");

                    DefaultTableModel model =
                            (DefaultTableModel) panier.getTable().getModel();

                    model.setRowCount(0);

                    // Actualiser les stocks dans les résultats
                    controllerBase.actualiserResultats();

                } else {

                    JOptionPane.showMessageDialog(
                            null,
                            "Impossible de valider le panier. " +
                                    "Vérifiez que le stock est suffisant.",
                            "Attention",
                            JOptionPane.WARNING_MESSAGE
                    );
                }

            } catch (SQLException ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        null,
                        "Erreur SQL lors du paiement : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
