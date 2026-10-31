package model.entity;

public class CaddyItem implements Entity {
    private static final long serialVersionUID = 1L;

    private int id;
    private int caddyId;
    private int bookId;
    private int quantity;

    public CaddyItem(int caddyId, int bookId, int quantity) {
        this(0, caddyId, bookId, quantity);
    }

    public CaddyItem(int id, int caddyId, int bookId, int quantity) {
        this.id = id;
        this.caddyId = caddyId;
        this.bookId = bookId;
        this.quantity = quantity;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }

    public int getCaddyId() { return caddyId; }
    public int getBookId() { return bookId; }
    public int getQuantity() { return quantity; }
}