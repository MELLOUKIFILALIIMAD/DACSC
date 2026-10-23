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
                case "AuteurSection":
                case "SubjectSection":
                case "BaseSection":
                case "Encodage":
                    section = container.getName();
                    break;
            }
        }

        for (Component component : container.getComponents()) {

            if (component instanceof JButton button) {

                button.setFont(new Font("Arial", Font.BOLD, 14));
                button.setFocusPainted(false);

                if ("AuteurSection".equals(section) ||
                        "SubjectSection".equals(section)) {

                    button.setBackground(Color.CYAN);
                    button.setForeground(Color.BLACK);
                }

                else if ("BaseSection".equals(section)) {

                    button.setBackground(Color.ORANGE);
                    button.setForeground(Color.BLACK);
                }

                else if ("Encodage".equals(section)) {

                    button.setBackground(Color.GREEN);
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