package model.viewmodel;

import model.controller.ControllerBase;

public interface BaseVM {
    void setControllerBase(ControllerBase controllerBase);

    void clearAll();
}
