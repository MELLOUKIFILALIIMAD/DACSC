package model.viewmodel;


public interface MainApp {

    // void setCardLayout(String layout);

    // void setControllerUser(ControllerUser c);

    void showMessage(String message);
    void showErrorMessage(String message);

    // String[] promptForLogin() throws UserException, ClientException;
    // String[] promptForRegister();

    // User getCurrentUser();
    // void setCurrentUser(User user);

    // void setMenuItem(int connected);
    // void setNavButtons(int role);

    void run();
}