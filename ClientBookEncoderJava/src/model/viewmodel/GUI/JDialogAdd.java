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

    private boolean isAut = false;


    public JDialogAdd(Window parent, boolean isAuteur) {
        super(parent, isAuteur ? "Ajouter Auteur" : "Ajouter Sujet", ModalityType.APPLICATION_MODAL);
        setSize(300, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        isAut = isAuteur;

        if (!isAuteur) {
            firstLabel.setText("Nom du sujet :");
            secondLabel.setVisible(false);
            secondText.setVisible(false);
        }
        else
        {
            firstLabel.setText("Nom de l'auteur :");
            secondLabel.setText("Prénom de l'auteur :");
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
        if (isAuteur)
        {
            Prenom = secondText.getText();
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

    public boolean isAuteur() {
        return isAut;
    }
}
