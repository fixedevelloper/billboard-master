package com.cscreativ.billboard.owner.domain.valueobject;

import java.util.Objects;

public class OwnerDetails {
    private final String companyName;
    private final String registrationNumber;
    private final String contactEmail;
    private final String phoneNumber;

    public OwnerDetails(String companyName, String registrationNumber, String contactEmail, String phoneNumber) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'entreprise/régisseur est obligatoire");
        }
        if (contactEmail == null || contactEmail.isBlank()) {
            throw new IllegalArgumentException("L'email de contact est obligatoire");
        }
        this.companyName = companyName.trim();
        this.registrationNumber = registrationNumber != null ? registrationNumber.trim() : "";
        this.contactEmail = contactEmail.trim();
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : "";
    }

    public String getCompanyName() { return companyName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getContactEmail() { return contactEmail; }
    public String getPhoneNumber() { return phoneNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwnerDetails details = (OwnerDetails) o;
        return Objects.equals(companyName, details.companyName) &&
               Objects.equals(registrationNumber, details.registrationNumber) &&
               Objects.equals(contactEmail, details.contactEmail) &&
               Objects.equals(phoneNumber, details.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, registrationNumber, contactEmail, phoneNumber);
    }
}
