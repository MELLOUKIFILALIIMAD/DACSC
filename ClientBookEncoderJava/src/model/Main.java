package model;

import model.viewmodel.GUI.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {


            AuthorSection authorSection = new AuthorSection();
            BookSection bookSection = new BookSection();
            SubjectSection subjectSection = new SubjectSection();
            BaseSection baseSection = new BaseSection();

            MainView mainView = new MainView(
                    baseSection,
                    authorSection,
                    bookSection,
                    subjectSection
            );

            mainView.run();
        });
    }
}