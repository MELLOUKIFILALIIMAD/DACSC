package model;

import model.controller.ControllerBase;
import model.controller.ControllerPanier;
import model.controller.ControllerResultat;
import model.network.ClientTCP;
import model.viewmodel.GUI.*;
import javax.swing.*;
import com.formdev.flatlaf.FlatIntelliJLaf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MainAchat {

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();

        Properties props = new Properties();

        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            System.err.println("Impossible de charger le fichier de configuration : " + e.getMessage());
            return;
        }

        String ip = props.getProperty("SERVER_IP", "localhost").trim();
        int port;

        try {
            port = Integer.parseInt(props.getProperty("PORT_PAYMENT").trim());
        } catch (NumberFormatException | NullPointerException e) {
            JOptionPane.showMessageDialog(null,
                    "PORT_PAYMENT manquant ou invalide dans config.properties",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final ClientTCP client = new ClientTCP(ip, port);

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
                        resultatSection, baseSection, client);
                new ControllerPanier(panierSection, controllerBase, client);
                new ControllerResultat(resultatSection, controllerBase, client);

                controllerBase.run();
            }
        });
    }
}