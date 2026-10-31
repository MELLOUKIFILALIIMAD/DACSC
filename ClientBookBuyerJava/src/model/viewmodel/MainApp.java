package model.viewmodel;


import model.controller.ControllerBase;

public interface MainApp {

    // void setCardLayout(String layout);

    void setControllerLivre(ControllerLivre controllerLivre);

    void showMessage(String message);
    void showErrorMessage(String message);

    String[] promptForLogin();
    // String[] promptForRegister();

    // User getCurrentUser();
    // void setCurrentUser(User user);

    // void setMenuItem(int connected);
    // void setNavButtons(int role);

    void run();
}