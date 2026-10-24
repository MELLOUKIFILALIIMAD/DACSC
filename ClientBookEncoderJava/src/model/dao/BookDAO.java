package model.dao;

import model.entity.Book;

import java.io.IOException;
import java.util.Locale;

public class BookDAO extends BaseDAO {

    public BookDAO(ServeurTCP serveur) {
        super(serveur);
    }

    // ADD_BOOK#titre#isbn#auteur#sujet#pages#annee#prix#stock
    //   ->  ADD_BOOK#ok#message  ou  ADD_BOOK#ko#raison
    // auteur = "Nom Prénom", prix avec '.' comme séparateur décimal
    public String insert(Book book) throws IOException {
        String auteur = book.getAuthor().getLastName() + " " + book.getAuthor().getFirstName();
        String prix = String.format(Locale.ROOT, "%.2f", book.getPrice());

        verifier(book.getTitle(), book.getIsbn(), auteur, book.getSubject().getName());

        // ***** Construction de la requete *****
        String requete = "ADD_BOOK#" + book.getTitle()
                + "#" + book.getIsbn()
                + "#" + auteur
                + "#" + book.getSubject().getName()
                + "#" + book.getPageCount()
                + "#" + book.getPublishYear()
                + "#" + prix
                + "#" + book.getStockQuantity();

        String[] champs = appeler("ADD_BOOK", requete);

        return champs.length > 2 ? champs[2] : "";
    }
}