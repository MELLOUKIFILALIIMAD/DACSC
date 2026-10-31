package model.controller;

import model.viewmodel.GUI.MainView;
import model.viewmodel.GUI.ResultatSection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerResultat implements ActionListener {
    private ResultatSection resultatSection;

    public ControllerResultat(ResultatSection resultatSection) {
        this.resultatSection = resultatSection;

        this.resultatSection.setControllerResultat(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}