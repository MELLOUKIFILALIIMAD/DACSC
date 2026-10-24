package model.viewmodel.GUI;

import javax.swing.*;
import java.awt.event.*;

public class JDialogLogin extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField passwordText;
    private JTextField LoginText;
    private JLabel loginLabel;
    private JLabel passwordLabel;

    private String Login;
    private String Password;

    public JDialogLogin(JFrame parent) {
        super(parent, "Login", true);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(parent);

        loginLabel.setText("Login");
        passwordLabel.setText("Password");
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
        Login = LoginText.getText();
        Password = passwordText.getText();
        dispose();
    }

    private void onCancel() {
        Login = null;
        Password = null;
        dispose();
    }

    public static void main(String[] args) {
        JDialogLogin dialog = new JDialogLogin(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public String getLogin() {
        return Login;
    }

    public String getPassword() {
        return Password;
    }
}
