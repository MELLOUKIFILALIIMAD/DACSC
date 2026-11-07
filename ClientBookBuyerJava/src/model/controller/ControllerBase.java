
package model.controller;

import model.dao.ClientDAO;
import model.entity.Client;
import model.network.ClientTCP;
import model.viewmodel.GUI.BaseSection;
import model.viewmodel.GUI.MainView;
import model.viewmodel.GUI.PanierSection;
import model.viewmodel.GUI.ResultatSection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ControllerBase implements ActionListener {

    private MainView mainView;
    private PanierSection panierSection;
    private ResultatSection resultatSection;
    private BaseSection baseSection;
    private ClientDAO clientDAO;
    private ClientTCP clientTCP;
    private Client loggedInClient;

    // Mémoriser les derniers critères de recherche
    private String dernierTitre = "";
    private String dernierAuteur = "";
    private String dernierSujet = "";
    private String dernierMaxPrix = "0";

    public ControllerBase(MainView mainView, PanierSection panierSection,
                          ResultatSection resultatSection, BaseSection baseSection,
                          ClientDAO clientDAO, ClientTCP clientTCP) {

        this.mainView = mainView;
        this.panierSection = panierSection;
        this.resultatSection = resultatSection;
        this.baseSection = baseSection;
        this.clientDAO = clientDAO;
        this.clientTCP = clientTCP;

        this.mainView.setControllerBase(this);
        this.baseSection.setControllerBase(this);
        this.panierSection.setControllerBase(this);
        this.resultatSection.setControllerBase(this);
    }

    public void run() {
        mainView.run();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String action = e.getActionCommand();

        if (action.equals(ControllerActions.Login)) {

            String[] u = mainView.promptForConnect();

            if (u == null) return;
            if (u.length < 2) return;
            if (u[0].trim().isEmpty()) return;
            if (u[1].trim().isEmpty()) return;

            String nom = u[0].trim();
            String prenom = u[1].trim();

            try {

                loggedInClient = clientDAO.findByName(nom, prenom);

                if (loggedInClient == null) {

                    loggedInClient = new Client();
                    loggedInClient.setName(nom);
                    loggedInClient.setFirstName(prenom);
                    loggedInClient.setAddress("");

                    clientDAO.insert(loggedInClient);
                }

                // Réactiver la connexion TCP si elle a été fermée
                clientTCP.reconnecter();

                mainView.setEnabled(true);
                panierSection.setEnabled(true);
                baseSection.setEnabled(true);
                resultatSection.setEnabled(true);

                actualiserPanier();

                JOptionPane.showMessageDialog(
                        mainView,
                        "Connecté avec succès ! ID client : " + loggedInClient.getId(),
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        mainView,
                        "Erreur lors de la connexion : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        if (action.equals(ControllerActions.Logout)) {

            loggedInClient = null;

            mainView.setEnabled(false);
            panierSection.setEnabled(false);
            baseSection.setEnabled(false);
            resultatSection.setEnabled(false);
        }

        if (action.equals(ControllerActions.Rechercher)) {

            String titre = baseSection.getTitreText().getText().trim();
            String auteur = baseSection.getAuteurText().getText().trim();
            String sujet = baseSection.getSujetText().getText().trim();
            String maxPrix = baseSection.getMaxPrixSpinner().getValue().toString();

            // Mémoriser les critères de recherche
            dernierTitre = titre;
            dernierAuteur = auteur;
            dernierSujet = sujet;
            dernierMaxPrix = maxPrix;

            try {

                String[] resultats = clientTCP.requete(
                        "SELECT_BOOK",
                        titre,
                        auteur,
                        sujet,
                        maxPrix
                );

                resultatSection.displayResults(resultats);

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        mainView,
                        "Erreur lors de la recherche : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public Client getLoggedInClient() {
        return loggedInClient;
    }

    public String getClientNumber() {

        if (loggedInClient == null) {
            return null;
        }

        return String.valueOf(loggedInClient.getId());
    }

    public void actualiserPanier() {

        if (loggedInClient == null) return;

        try {

            String[] resultats = clientTCP.requete(
                    "GET_CADDY",
                    String.valueOf(loggedInClient.getId())
            );

            panierSection.afficherPanier(resultats);

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    mainView,
                    "Erreur lors de la récupération du panier : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Actualiser les résultats et les stocks affichés
    public void actualiserResultats() {

        try {

            String[] resultats = clientTCP.requete(
                    "SELECT_BOOK",
                    dernierTitre,
                    dernierAuteur,
                    dernierSujet,
                    dernierMaxPrix
            );

            resultatSection.displayResults(resultats);

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    mainView,
                    "Erreur lors de l'actualisation des résultats : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void annulerSession() {

        loggedInClient = null;

        mainView.setEnabled(false);
        panierSection.setEnabled(false);
        baseSection.setEnabled(false);
        resultatSection.setEnabled(false);

        baseSection.viderChamps();

        panierSection.afficherPanier(new String[]{"0"});

        resultatSection.clearTable();

        JOptionPane.showMessageDialog(
                mainView,
                "Session annulée avec succès.",
                "Déconnexion",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    public void supprimerLivreDuPanier(String bookId) {
        if (loggedInClient == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Vous devez être connecté pour modifier le panier."
            );
            return;
        }

        try {
            clientTCP.requete(
                    "UPDATE_CADDY_ITEM",
                    getClientNumber(),
                    bookId,
                    "0"
            );

            actualiserPanier();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erreur lors de la suppression du livre : " + e.getMessage()
            );
        }
    }
}
