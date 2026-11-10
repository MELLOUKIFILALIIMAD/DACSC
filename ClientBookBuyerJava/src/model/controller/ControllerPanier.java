package model.controller;

import model.entity.Client;
import model.network.ClientException;
import model.network.ClientTCP;
import model.viewmodel.GUI.PanierSection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ControllerPanier implements ActionListener {

    private PanierSection panier;
    private ControllerBase controllerBase;
    private ClientTCP clientTCP;

    public ControllerPanier(PanierSection panier,
                            ControllerBase controllerBase,
                            ClientTCP clientTCP) {

        this.panier = panier;
        this.controllerBase = controllerBase;
        this.clientTCP = clientTCP;

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

                clientTCP.requete("PAY_CADDY", String.valueOf(currentClient.getId()));

                JOptionPane.showMessageDialog(
                        null,
                        "Paiement validé avec succès ! Merci pour votre achat.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );

                panier.getPrixTotal().setText("0");

                DefaultTableModel model =
                        (DefaultTableModel) panier.getTable().getModel();

                model.setRowCount(0);

                controllerBase.actualiserResultats();

            } catch (ClientException ex) {

                JOptionPane.showMessageDialog(
                        null,
                        "Impossible de valider le panier : " + ex.getMessage(),
                        "Attention",
                        JOptionPane.WARNING_MESSAGE
                );

            } catch (IOException ex) {

                JOptionPane.showMessageDialog(
                        null,
                        "Erreur réseau lors du paiement : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}