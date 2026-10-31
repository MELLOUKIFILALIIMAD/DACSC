package model.controller;

import model.dao.AuthorDAO;
import model.dao.SubjectDAO;
import model.dao.UserDAO;
import model.viewmodel.GUI.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerBase implements ActionListener {
    private BaseSection baseSection;
    private MainView mainView;
    private PanierSection panierSection;
    private ResultatSection bookSection;

    private UserDAO userDAO;
    private AuthorDAO authorDAO;
    private SubjectDAO subjectDAO;

    private String loginCourant = null;

    public void run() {
        mainView.run();
    }

    public ControllerBase(BaseSection baseSection, MainView mainView, PanierSection panierSection,
                          ResultatSection bookSection,
                          UserDAO userDAO, AuthorDAO authorDAO, SubjectDAO subjectDAO) {
        this.baseSection = baseSection;
        this.mainView = mainView;
        this.panierSection = panierSection;
        this.bookSection = bookSection;

        this.userDAO = userDAO;
        this.authorDAO = authorDAO;
        this.subjectDAO = subjectDAO;

        this.baseSection.setControllerBase(this);
        this.mainView.setControllerBase(this);
        this.panierSection.setControllerBase(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}