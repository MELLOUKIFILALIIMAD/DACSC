package model;

import model.controller.ControllerBase;
import model.controller.ControllerPanier;
import model.controller.ControllerResultat;
import model.viewmodel.GUI.*;
import javax.swing.*;
import com.formdev.flatlaf.FlatIntelliJLaf;

import java.security.cert.CertPath;

public class Main {

    // IP de la machine du serveur Encodage et PORT_ENCODING (voir configuration.txt côté serveur)

    private static final int PORT_ENCODING = 5000;

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();


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

                ControllerBase controllerBase = new ControllerBase(mainView, panierSection, resultatSection, baseSection);
                new ControllerPanier(panierSection);
                new ControllerResultat(resultatSection);


                controllerBase.run();
            }
        });
    }
}