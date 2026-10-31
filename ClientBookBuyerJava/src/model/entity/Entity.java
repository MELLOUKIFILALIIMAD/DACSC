package model.entity;

import java.io.Serializable;

public interface Entity extends Serializable {
    int getId();

    void setId(int id);
}
