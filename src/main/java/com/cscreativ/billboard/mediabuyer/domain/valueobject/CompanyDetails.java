package com.cscreativ.billboard.mediabuyer.domain.valueobject;

import java.util.Objects;

public class CompanyDetails {
    private final String companyName;
    private final String taxId;
    private final String contactEmail;
    private final String phoneNumber;

    public CompanyDetails(String companyName, String taxId, String contactEmail, String phoneNumber) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'entreprise est obligatoire");
        }
        if (contactEmail == null || contactEmail.isBlank()) {
            throw new IllegalArgumentException("L'email de contact est obligatoire");
        }
        this.companyName = companyName.trim();
        this.taxId = taxId != null ? taxId.trim() : "";
        this.contactEmail = contactEmail.trim();
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : "";
    }

    public String getCompanyName() { return companyName; }
    public String getTaxId() { return taxId; }
    public String getContactEmail() { return contactEmail; }
    public String getPhoneNumber() { return phoneNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyDetails details = (CompanyDetails) o;
        return Objects.equals(companyName, details.companyName) &&
               Objects.equals(taxId, details.taxId) &&
               Objects.equals(contactEmail, details.contactEmail) &&
               Objects.equals(phoneNumber, details.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, taxId, contactEmail, phoneNumber);
    }
}
