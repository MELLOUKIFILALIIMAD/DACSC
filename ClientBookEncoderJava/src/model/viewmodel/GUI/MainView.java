package model.viewmodel.GUI;

import model.viewmodel.GUI.common.Style;
import model.viewmodel.MainApp;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame implements MainApp {

    private JPanel panel1;
    private JPanel Titre;
    private JPanel AuteurSujet;
    private JPanel panel4;
    private JPanel panel2;
    private JPanel panel3;
    private JButton buttonEncodage;
    private JPanel Encodage;

    public MainView(BaseSection baseSection,
                    AuthorSection authorSection,
                    BookSection bookSection,
                    SubjectSection subjectSection) {

        Encodage.setName("Encodage");

        setContentPane(panel1);
        setTitle("Client Encodage");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        // Section d'encodage
        Titre.setLayout(new BorderLayout());
        Titre.add(baseSection.getMainPanel(), BorderLayout.NORTH);

        // Section auteurs
        panel2.setLayout(new BorderLayout());
        panel2.add(authorSection.getMainPanel(), BorderLayout.CENTER);

        // Section livres
        panel3.setLayout(new BorderLayout());
        panel3.add(subjectSection.getMainPanel(), BorderLayout.CENTER);

        // Section sujets
        panel4.setLayout(new BorderLayout());
        panel4.add(bookSection.getMainPanel(), BorderLayout.CENTER);

        Style.applyStyle(panel1);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @Override
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void run() {
        setVisible(true);
    }

}