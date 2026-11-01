package model.viewmodel.GUI;

import model.controller.ControllerActions;
import model.controller.ControllerBase;
import model.controller.ControllerLivre;
import model.viewmodel.GUI.common.Style;
import model.viewmodel.MainVM;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame implements MainVM {

    private JPanel panel1;
    private JPanel Titre;
    private JPanel AuteurSujet;
    private JPanel panel4;
    private JPanel panel2;
    private JPanel panel3;
    private JButton buttonEncodage;
    private JPanel Encodage;
    private JMenuItem menuItemLogin;
    private JMenuItem menuItemLogout;

    public MainView(BaseSection baseSection,
                    AuthorSection authorSection,
                    BookSection bookSection,
                    SubjectSection subjectSection) {


        Encodage.setName("Encodage");

        setContentPane(panel1);
        setTitle("Client Encodage");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 400);
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

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Session");
        menuItemLogin = new JMenuItem(ControllerActions.Login);
        menu.add(menuItemLogin);
        menu.addSeparator();
        menuItemLogout = new JMenuItem(ControllerActions.Logout);
        menu.add(menuItemLogout);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        buttonEncodage.setText(ControllerActions.AjouterLivre);

        Style.applyStyle(panel1);
    }

    public JButton getButtonEncodage() {
        return buttonEncodage;
    }

    public JMenuItem getMenuItemLogin() {
        return menuItemLogin;
    }

    public JMenuItem getMenuItemLogout() {
        return menuItemLogout;
    }

    public void setEnabled(boolean b) {
        buttonEncodage.setEnabled(b);
        menuItemLogin.setEnabled(!b);
        menuItemLogout.setEnabled(b);
    }

    @Override
    public void setControllerLivre(ControllerLivre c) {
        buttonEncodage.addActionListener(c);
    }

    public void setControllerBase(ControllerBase c) {
        menuItemLogin.addActionListener(c);
        menuItemLogout.addActionListener(c);
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
    public String[] promptForLogin() {
        JDialogLogin dialog = new JDialogLogin(this);
        dialog.pack();
        dialog.setVisible(true);

        String Login = dialog.getLogin();
        String Password = dialog.getPassword();

        return new String[]{Login, Password};
    }

    @Override
    public void run() {
        setVisible(true);
    }

}