package model.viewmodel.GUI;

import javax.swing.*;
import java.awt.event.*;

public class JDialogLogin extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField prenomText;
    private JTextField nomText;
    private JLabel nomLabel;
    private JLabel prenomLabel;

    private String prenom;
    private String nom;

    public JDialogLogin(JFrame parent) {
        super(parent, "Session client", true);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(parent);

        nomLabel.setText("Nom");
        prenomLabel.setText("Prenom");
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        nom = nomText.getText();
        prenom = prenomText.getText();
        dispose();
    }

    private void onCancel() {
        prenom = null;
        nom = null;
        dispose();
    }

    public static void main(String[] args) {
        JDialogLogin dialog = new JDialogLogin(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }
}
