package model.viewmodel;

import model.controller.ControllerBase;

public interface BaseApp {
    void setControllerBase(ControllerBase controllerBase);

    void clearAll();
}
