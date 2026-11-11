package model.protocol;

import java.io.Serializable;

public class Requete implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String[] champs;

    public Requete(String[] champs) {
        this.champs = champs;
    }

    public String[] getChamps() {
        return champs;
    }
}