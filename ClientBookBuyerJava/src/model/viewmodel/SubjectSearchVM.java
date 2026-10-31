package model.viewmodel;

import model.controller.ControllerSujet;

public interface SubjectSearchVM {
    void setControllerSujet(ControllerSujet controllerSujet);

    String promptForAddSubject();
}
