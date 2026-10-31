package model.viewmodel.GUI;

import model.controller.ControllerActions;
import model.controller.ControllerBase;
import model.viewmodel.GUI.common.Style;
import model.viewmodel.MainApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainView extends JFrame implements MainApp {

    private JPanel panel1;
    private JPanel Titre;
    private JPanel panel4;
    private JPanel panel2;
    private JLabel clientLabel;
    private JMenuItem menuItemLogin;
    private JMenuItem menuItemLogout;

    public MainView(BaseSection baseSection,
                    PanierSection panierSection,
                    ResultatSection bookSection) {

        setContentPane(panel1);
        setTitle("Client Achat");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);

        // Section d'encodage
        Titre.setLayout(new BorderLayout());
        Titre.add(baseSection.getMainPanel(), BorderLayout.NORTH);

        // Section auteurs
        panel2.setLayout(new BorderLayout());
        panel2.add(panierSection.getMainPanel(), BorderLayout.CENTER);

        // Section sujets
        panel4.setLayout(new BorderLayout());
        panel4.add(bookSection.getMainPanel(), BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Session");
        menuItemLogin = new JMenuItem(ControllerActions.Login);
        menu.add(menuItemLogin);
        menu.addSeparator();
        menuItemLogout = new JMenuItem(ControllerActions.Logout);
        menu.add(menuItemLogout);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        Style.applyStyle(panel1);
    }

    public JMenuItem getMenuItemLogin() {
        return menuItemLogin;
    }

    public JMenuItem getMenuItemLogout() {
        return menuItemLogout;
    }

    public void setEnabled(boolean b) {
        menuItemLogin.setEnabled(!b);
        menuItemLogout.setEnabled(b);
    }

    @Override
    public void setControllerBase(ControllerBase c) {
        menuItemLogin.addActionListener((ActionListener) c);
        menuItemLogout.addActionListener((ActionListener) c);
    }

    @Override
    public String[] promptForConnect() {
        JDialogLogin dialog = new JDialogLogin(this);
        dialog.pack();
        dialog.setVisible(true);

        String Nom = dialog.getNom();
        String Prenom = dialog.getPrenom();

        return new String[]{Nom, Prenom};
    }

    @Override
    public void run() {
        setVisible(true);
    }

}