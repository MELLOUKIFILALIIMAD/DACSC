package model.viewmodel.GUI;

import javax.swing.*;
import java.awt.event.*;

public class JDialogServeur extends JDialog {
    private JPanel contentPane;
    private JLabel ipLabel;
    private JButton buttonImad;
    private JButton buttonIssam;
    private String ip;

    public JDialogServeur(JFrame parent) {
        super(parent, "Serveur", true);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(null);
        setSize(200, 200);
        setLocationRelativeTo(parent);

        ipLabel.setText("Quelle serveur ?");
        buttonImad.setText("Imad");
        buttonIssam.setText("Issam");

        buttonImad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerImad();
            }
        });

        buttonIssam.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerIssam();
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

    private void ServerImad() {
        ip = "192.168.142.131";
        dispose();
    }

    private void ServerIssam() {
        ip = "192.168.239.131";
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public String getIp() {
        return ip;
    }

    public static void main(String[] args) {
        JDialogServeur dialog = new JDialogServeur(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}