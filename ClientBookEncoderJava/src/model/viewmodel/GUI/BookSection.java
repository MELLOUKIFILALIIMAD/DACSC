package model.viewmodel.GUI;

import javax.swing.*;

public class BookSection {
    private JPanel Book;
    private JTable tableBooks;
    private JScrollPane ListeLivre;

    public BookSection()
    {
        Book.setName("BookSection");
    }
    public JPanel getMainPanel() {
        return Book;
    }
}
