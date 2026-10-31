package model.dao;

import model.entity.Client;
import model.network.ClientTCP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO extends BaseDAO {

    public SubjectDAO(ClientTCP serveur) {
        super(serveur);
    }

    // GET_SUBJECTS  ->  GET_SUBJECTS#ok#ids#noms   (listes du type "1;2;3;")
    public List<Client> findAll() throws IOException {
        String[] champs = appeler("GET_SUBJECTS", "GET_SUBJECTS");

        List<Client> subjects = new ArrayList<>();

        if (champs.length != 4) {
            throw new IOException("Réponse GET_SUBJECTS invalide");
        }

        if (!champs[2].isEmpty()) {
            String[] ids = champs[2].split(";");
            String[] noms = champs[3].split(";");

            for (int i = 0; i < ids.length; i++) {
                subjects.add(new Client(Integer.parseInt(ids[i]), noms[i]));
            }
        }

        return subjects;
    }

    // ADD_SUBJECT#nom  ->  ADD_SUBJECT#ok#message  ou  ADD_SUBJECT#ko#raison
    public String insert(Client subject) throws IOException {
        verifier(subject.getName());

        // ***** Construction de la requete *****
        String requete = "ADD_SUBJECT#" + subject.getName();

        String[] champs = appeler("ADD_SUBJECT", requete);
        if (champs.length > 2) {
            return champs[2];
        } else {
            return "";
        }
    }
}