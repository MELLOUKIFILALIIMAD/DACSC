package model.entity;

public class Book implements Entity {
    private int id;
    private Author author;
    private Subject subject;
    private String title;
    private String isbn;
    private int pageCount;
    private int stockQuantity;
    private double price;
    private int publishYear;

    public Book(Author author, Subject subject, String title, String isbn,
                int pageCount, int stockQuantity, double price, int publishYear) {
        this.author = author;
        this.subject = subject;
        this.title = title;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.publishYear = publishYear;
    }

    @Override
    public int getId() { return id; }
    @Override
    public void setId(int id) { this.id = id; }

    public Author getAuthor() { return author; }
    public Subject getSubject() { return subject; }
    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public int getPageCount() { return pageCount; }
    public int getStockQuantity() { return stockQuantity; }
    public double getPrice() { return price; }
    public int getPublishYear() { return publishYear; }

    @Override
    public String toString() {
        return title + " (" + publishYear + ")";
    }
}