package model.viewmodel;


import model.controller.ControllerBase;

public interface MainVM {
    String[] promptForConnect();

    void setControllerBase(ControllerBase c);
    void run();
}