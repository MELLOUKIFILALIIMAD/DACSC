package model;

import model.controller.ControllerBase;
import model.controller.ControllerPanier;
import model.controller.ControllerResultat;
import model.dao.CaddyDAO;
import model.dao.CaddyItemDAO;
import model.dao.ClientDAO;
import model.dao.ConnectDB;
import model.entity.Client;
import model.network.ClientTCP;
import model.viewmodel.GUI.*;
import javax.swing.*;
import com.formdev.flatlaf.FlatIntelliJLaf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MainAchat {

    // IP de la machine du serveur Encodage et PORT_ENCODING (voir configuration.txt côté serveur)

    private static final int PORT_ENCODING = 5000;

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        ClientTCP client = new ClientTCP();

        try {
            client.connecter("localhost", PORT_ENCODING);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Impossible de joindre le serveur " + "localhost" + ":" + PORT_ENCODING
                            + "\n" + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Properties props = new Properties();

        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            System.err.println("Impossible de charger le fichier de configuration : " + e.getMessage());
            return;
        }

        ConnectDB.init(props);
        final CaddyDAO caddyDAO = new CaddyDAO(client);
        final CaddyItemDAO caddyItemDAO = new CaddyItemDAO(client);
        final ClientDAO clientDAO = new ClientDAO(client);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PanierSection panierSection = new PanierSection();
                ResultatSection resultatSection = new ResultatSection();
                BaseSection baseSection = new BaseSection();
                MainView mainView = new MainView(baseSection, panierSection, resultatSection);

                mainView.setEnabled(false);
                baseSection.setEnabled(false);
                panierSection.setEnabled(false);
                resultatSection.setEnabled(false);

                ControllerBase controllerBase = new ControllerBase(mainView, panierSection,
                                                        resultatSection, baseSection, clientDAO, client);
                new ControllerPanier(panierSection, controllerBase, caddyDAO);
                new ControllerResultat(resultatSection, controllerBase, client);


                controllerBase.run();
            }
        });
    }
}