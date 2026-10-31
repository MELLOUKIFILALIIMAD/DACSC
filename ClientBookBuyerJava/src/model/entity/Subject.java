package model.entity;

public class Subject implements Entity {
    private int id;
    private String name;

    public Subject(String name) {
        this(0, name);
    }

    public Subject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() { return id; }
    @Override
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }
}