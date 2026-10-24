package model.viewmodel.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JDialogAdd extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField firstText;
    private JTextField secondText;
    private JLabel firstLabel;
    private JLabel secondLabel;

    private String Nom;
    private String Prenom;
    private JTextField dateNaissanceText;
    private JLabel dateNaissanceLabel;
    private JLabel datFormat;
    private String DateNaissance;

    private boolean isAut = false;


    public JDialogAdd(Window parent, boolean isAuteur) {
        super(parent, isAuteur ? "Ajouter Auteur" : "Ajouter Sujet", ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        isAut = isAuteur;

        if (!isAuteur) {
            setSize(300, 150);
            firstLabel.setText("Nom du sujet :");
            secondLabel.setVisible(false);
            secondText.setVisible(false);
            dateNaissanceLabel.setVisible(false);
            dateNaissanceText.setVisible(false);
            datFormat.setVisible(false);
        }
        else
        {
            setSize(400, 200);
            firstLabel.setText("Nom de l'auteur :");
            secondLabel.setText("Prénom de l'auteur :");
            datFormat.setText("Exemple de date de naissance: 2026-10-24");
            dateNaissanceLabel.setText("Date de naissance :");
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(isAuteur);
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

    private void onOK(boolean isAuteur) {
        Nom = firstText.getText();

        if (isAuteur) {
            Prenom = secondText.getText();
            DateNaissance = dateNaissanceText.getText().trim();
        }

        dispose();
    }

    private void onCancel() {

        dispose();
    }

    public static void main(String[] args) {
        JDialogAdd dialog = new JDialogAdd(null, true);
        dialog.setVisible(true);
        System.exit(0);
    }

    public String getNom() {
        return Nom;
    }

    public String getPrenom() {
        if (isAut)
        {
            return Prenom;
        }
        else
            return null;
    }
    public String getDateNaissance() {
        return DateNaissance;
    }
    public boolean isAuteur() {
        return isAut;
    }

}
