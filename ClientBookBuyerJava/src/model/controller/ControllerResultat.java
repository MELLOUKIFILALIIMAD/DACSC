package model.controller;

import model.network.ClientException;
import model.network.ClientTCP;
import model.viewmodel.GUI.ResultatSection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ControllerResultat implements ActionListener {
    private ResultatSection resultatSection;
    private ControllerBase controllerBase;
    private ClientTCP clientTCP;

    public ControllerResultat(ResultatSection resultatSection, ControllerBase controllerBase, ClientTCP clientTCP) {
        this.resultatSection = resultatSection;
        this.controllerBase = controllerBase;
        this.clientTCP = clientTCP;

        this.resultatSection.setControllerResultat(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if (action.equals(ControllerActions.AjouterPanier)) {
            String clientNumber = controllerBase.getClientNumber();
            if (clientNumber == null) {
                JOptionPane.showMessageDialog(resultatSection.getMainPanel(),
                        "Aucun client connecté.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTable table = resultatSection.getTableBooks();
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(resultatSection.getMainPanel(),
                        "Veuillez sélectionner un livre dans le tableau.", "Attention", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object bookIdObj = table.getValueAt(selectedRow, 0);
            String bookId = bookIdObj.toString();

            int quantity = resultatSection.getSliderQte().getValue();
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(resultatSection.getMainPanel(),
                        "Veuillez sélectionner une quantité supérieure à 0.", "Attention", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                clientTCP.requete(
                        "ADD_CADDY_ITEM",
                        clientNumber,
                        bookId,
                        String.valueOf(quantity)
                );

                controllerBase.actualiserPanier();

                JOptionPane.showMessageDialog(resultatSection.getMainPanel(),
                        "Livre ajouté au panier avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);

            } catch (ClientException ex) {
                JOptionPane.showMessageDialog(resultatSection.getMainPanel(),
                        "Erreur serveur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(resultatSection.getMainPanel(),
                        "Erreur réseau : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getActionCommand().equals(ControllerActions.ViderPanier)) {

            String clientNumber = controllerBase.getClientNumber();

            if (clientNumber == null) {
                JOptionPane.showMessageDialog(
                        null,
                        "Aucun client connecté !"
                );
                return;
            }

            int choix = JOptionPane.showConfirmDialog(
                    null,
                    "Voulez-vous vraiment vider votre panier ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );

            if (choix == JOptionPane.YES_OPTION) {

                try {
                    clientTCP.requete("CANCEL_CADDY", clientNumber);

                    controllerBase.actualiserPanier();

                    JOptionPane.showMessageDialog(
                            null,
                            "Votre panier a été vidé.",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Erreur lors du vidage : " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }

        if (action.equals(ControllerActions.AnnulerSession)) {
            controllerBase.annulerSession();
        }
    }
}