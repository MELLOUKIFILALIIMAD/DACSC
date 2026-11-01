package model.entity;

public class Client implements EntityAchat {
    private static final long serialVersionUID = 1L;

    private int id;          // = le numéro de client
    private String name;
    private String firstName;
    private String address;  // peut être null

    public Client(String name, String firstName, String address) {
        this(0, name, firstName, address);
    }

    public Client(int id, String name, String firstName, String address) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.address = address;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public String getFirstName() { return firstName; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return name + " " + firstName + " (n° " + id + ")";
    }
}