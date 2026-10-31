package model.controller;

import model.dao.BookDAO;
import model.entity.Caddy;
import model.viewmodel.GUI.*;

import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerLivre implements ActionListener {
    private MainView mainView;
    private BookDAO bookDao;
    private BookSection bookSection;
    private AuthorSection authorSection;
    private SubjectSection subjectSection;
    private BaseSection baseSection;

    public ControllerLivre(MainView mainView, BookDAO bookDao, BookSection bookSection, AuthorSection authorSection, SubjectSection subjectSection, BaseSection baseSection) {
        this.mainView = mainView;
        this.bookDao = bookDao;
        this.bookSection = bookSection;
        this.authorSection = authorSection;
        this.subjectSection = subjectSection;
        this.baseSection = baseSection;

        this.mainView.setControllerLivre(this);
    }
    public void addBookToTable(Caddy book) {
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
        if (e.getActionCommand().equals(ControllerActions.AjouterLivre)) {
            try {
                Caddy book = new Caddy(
                        authorSection.getSelectedAuthor(),
                        subjectSection.getSelectedSubject(),
                        baseSection.getTitreText().getText(),
                        baseSection.getISBNText().getText(),
                        (Integer) baseSection.getPagesSpinner().getValue(),
                        (Integer) baseSection.getStockSpinner().getValue(),
                        ((Number) baseSection.getPrixSpinner().getValue()).doubleValue(),
                        (Integer) baseSection.getAnneeSpinner().getValue()
                );

                String message = bookDao.insert(book);

                addBookToTable(book);
                baseSection.clearAll();

                if (message != null && !message.isEmpty()) {
                    mainView.showMessage(message);
                }

            } catch (Exception ex) {
                mainView.showErrorMessage(ex.getMessage());
            }
        }

    }
}
