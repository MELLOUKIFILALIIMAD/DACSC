package model.dao;

import model.entity.Author;
import model.network.ClientTCP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO extends BaseDAO {

    public AuthorDAO(ClientTCP client) {
        super(client);
    }


    // GET_AUTHORS  ->  GET_AUTHORS#ok#ids#noms#prenoms   (listes du type "1;2;3;")
    public List<Author> findAll() throws IOException {
        String[] champs = appeler("GET_AUTHORS", "GET_AUTHORS");

        List<Author> authors = new ArrayList<>();

        if (champs.length != 5) {
            throw new IOException("Réponse GET_AUTHORS invalide");
        }

        if (!champs[2].isEmpty()) {
            String[] ids = champs[2].split(";");
            String[] noms = champs[3].split(";");
            String[] prenoms = champs[4].split(";");

            for (int i = 0; i < ids.length; i++) {
                authors.add(new Author(Integer.parseInt(ids[i]), noms[i], prenoms[i], null));
            }
        }

        return authors;
    }

    // ADD_AUTHOR#nom#prenom#yyyy-mm-dd  ->  ADD_AUTHOR#ok#message  ou  ADD_AUTHOR#ko#raison
    public String insert(Author author) throws IOException {
        verifier(author.getLastName(), author.getFirstName());

        // ***** Construction de la requete *****
        String requete = "ADD_AUTHOR#" + author.getLastName()
                + "#" + author.getFirstName()
                + "#" + author.getBirthDate();      // format yyyy-MM-dd

        String[] champs = appeler("ADD_AUTHOR", requete);

        if (champs.length > 2) {
            return champs[2];
        } else {
            return "";
        }
    }
}