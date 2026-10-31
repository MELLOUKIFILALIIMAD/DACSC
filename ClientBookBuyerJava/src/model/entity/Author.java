package model.entity;

import java.time.LocalDate;

public class Author implements Entity {
    private int id;
    private String lastName;
    private String firstName;
    private LocalDate birthDate;

    public Author(String lastName, String firstName, LocalDate birthDate) {
        this(0, lastName, firstName, birthDate);
    }

    public Author(int id, String lastName, String firstName, LocalDate birthDate) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
    }

    @Override
    public int getId() { return id; }
    @Override
    public void setId(int id) { this.id = id; }

    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public LocalDate getBirthDate() { return birthDate; }

    @Override
    public String toString() {
        return lastName + " " + firstName;
    }
}