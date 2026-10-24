package model;

import model.controller.ControllerAuteur;
import model.controller.ControllerBase;
import model.controller.ControllerSujet;
import model.viewmodel.GUI.*;
import javax.swing.*;
import com.formdev.flatlaf.FlatIntelliJLaf;

public class Main {
    public static void main(String[] args) {
        FlatIntelliJLaf.setup();

        SwingUtilities.invokeLater(() -> {


            AuthorSection authorSection = new AuthorSection();
            BookSection bookSection = new BookSection();
            SubjectSection subjectSection = new SubjectSection();
            BaseSection baseSection = new BaseSection();
            MainView mainView = new MainView(baseSection,authorSection,bookSection,subjectSection);

            mainView.setEnabled(false);
            baseSection.setEnabled(false);
            authorSection.setEnabled(false);
            subjectSection.setEnabled(false);


            ControllerBase controllerBase = new ControllerBase(baseSection, mainView, authorSection, bookSection, subjectSection);

            new ControllerSujet(subjectSection);

            new ControllerAuteur(authorSection);

            controllerBase.run();
        });
    }
}