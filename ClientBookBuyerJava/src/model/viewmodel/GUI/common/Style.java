package model.viewmodel.GUI.common;

import javax.swing.*;
import java.awt.*;

public class Style {

    public static void applyStyle(Container container) {
        applyStyle(container, null);
    }

    private static void applyStyle(Container container, String section) {

        // Identifier la section actuelle
        if (container.getName() != null) {
            switch (container.getName()) {
                case "PanierSection":
                case "ResultatSection":
                case "BaseSection":
                    section = container.getName();
                    break;
            }
        }

        for (Component component : container.getComponents()) {

            if (component instanceof JButton button) {

                button.setFont(new Font("Arial", Font.BOLD, 14));

                if ("Rechercher".equals(button.getText()) || "Ajouter au panier".equals(button.getText())) {
                    button.setBackground(Color.CYAN);
                    button.setForeground(Color.BLACK);
                }
                else if ("Valider le panier".equals(button.getText())) {
                    button.setBackground(Color.GREEN);
                    button.setForeground(Color.BLACK);
                }
                else if ("Annuler la session".equals(button.getText())) {
                    button.setBackground(Color.RED);
                    button.setForeground(Color.BLACK);
                }
                else if ("Vider le panier".equals(button.getText())) {
                    button.setBackground(Color.GRAY);
                    button.setForeground(Color.BLACK);
                }
            }

            if (component instanceof JComboBox<?> comboBox) {
                comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
            }

            if (component instanceof JTable table) {
                table.setRowHeight(25);
            }

            if (component instanceof Container child) {
                applyStyle(child, section);
            }
        }
    }
}