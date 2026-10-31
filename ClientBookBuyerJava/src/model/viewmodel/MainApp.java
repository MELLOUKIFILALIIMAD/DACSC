package model.viewmodel;


import model.controller.ControllerBase;

public interface MainApp {
    String[] promptForConnect();

    void setControllerBase(ControllerBase c);
    void run();
}