package model.viewmodel;

import model.controller.ControllerBase;
import model.controller.ControllerResultat;
import model.viewmodel.GUI.BaseSection;

public interface ResultatVM {

    void setControllerResultat(ControllerResultat controller);
    void setControllerBase(ControllerBase controller);
}
