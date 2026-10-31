package model.controller;

import model.dao.BookDAO;
import model.entity.Book;
import model.viewmodel.GUI.*;

import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerLivre implements ActionListener {
    private MainView mainView;
    private BookDAO bookDao;
    private ResultatSection bookSection;
    private PanierSection panierSection;
    private BaseSection baseSection;

    public ControllerLivre(MainView mainView, BookDAO bookDao, ResultatSection bookSection, PanierSection panierSection, BaseSection baseSection) {
        this.mainView = mainView;
        this.bookDao = bookDao;
        this.bookSection = bookSection;
        this.panierSection = panierSection;
        this.baseSection = baseSection;

        this.mainView.setControllerLivre(this);
    }

    public void addBookToTable(Book book) {
        DefaultTableModel model = bookSection.getTableModel();
        String authorName = book.getAuthor().getLastName() + " " + book.getAuthor().getFirstName();

        model.addRow(new Object[]{
                book.getTitle(),
                book.getIsbn(),
                authorName,
                book.getSubject().getName(),
                book.getPublishYear(),
                String.format("%.2f €", book.getPrice()),
                book.getStockQuantity()
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}