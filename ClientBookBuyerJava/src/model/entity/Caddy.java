package model.entity;

import java.time.LocalDateTime;

public class Caddy implements EntityAchat {
    private static final long serialVersionUID = 1L;

    private int id;
    private int clientId;
    private LocalDateTime date;
    private double amount;
    private boolean payed;

    // Nouveau panier (pas encore en base)
    public Caddy(int clientId) {
        this(0, clientId, LocalDateTime.now(), 0.0, false);
    }

    public Caddy(int id, int clientId, LocalDateTime date, double amount, boolean payed) {
        this.id = id;
        this.clientId = clientId;
        this.date = date;
        this.amount = amount;
        this.payed = payed;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public LocalDateTime getDate() { return date; }
    public double getAmount() { return amount; }
    public boolean isPayed() { return payed; }
}