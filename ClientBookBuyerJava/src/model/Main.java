package model;

import model.controller.ControllerAuteur;
import model.controller.ControllerBase;
import model.controller.ControllerLivre;
import model.controller.ControllerSujet;
import model.dao.*;
import model.viewmodel.GUI.*;
import javax.swing.*;
import com.formdev.flatlaf.FlatIntelliJLaf;

import java.io.IOException;

public class Main {

    // IP de la machine du serveur Encodage et PORT_ENCODING (voir configuration.txt côté serveur)

    private static final int PORT_ENCODING = 5000;

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PanierSection panierSection = new PanierSection();
                ResultatSection bookSection = new ResultatSection();
                BaseSection baseSection = new BaseSection();
                MainView mainView = new MainView(baseSection, panierSection, bookSection);

                mainView.setEnabled(false);
                baseSection.setEnabled(false);
                panierSection.setEnabled(false);

                mainView.run();
            }
        });
    }
}