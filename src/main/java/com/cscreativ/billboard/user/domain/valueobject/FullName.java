package com.cscreativ.billboard.user.domain.valueobject;

import java.util.Objects;

public class FullName {
    private final String firstName;
    private final String lastName;

    public FullName(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFormattedName() { return firstName + " " + lastName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullName fullName = (FullName) o;
        return Objects.equals(firstName, fullName.firstName) && Objects.equals(lastName, fullName.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
}
