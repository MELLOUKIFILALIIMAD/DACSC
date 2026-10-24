package model.controller;

import model.viewmodel.GUI.AuthorSection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerAuteur implements ActionListener {
    private AuthorSection author;
    private JFrame frame;
    public ControllerAuteur(AuthorSection author) {
        this.author = author;

        this.author.setControllerAuteur(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ControllerActions.AjouterAuteur)) {
            author.promptForAddAuthor();
        }
    }
}
