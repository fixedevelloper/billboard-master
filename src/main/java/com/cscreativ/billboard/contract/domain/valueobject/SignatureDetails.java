package com.cscreativ.billboard.contract.domain.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;

public class SignatureDetails {
    private final String signedBy;
    private final String ipAddress;
    private final LocalDateTime signedAt;

    public SignatureDetails(String signedBy, String ipAddress, LocalDateTime signedAt) {
        if (signedBy == null || signedBy.isBlank()) {
            throw new IllegalArgumentException("Le nom/identifiant du signataire est obligatoire");
        }
        this.signedBy = signedBy.trim();
        this.ipAddress = ipAddress != null ? ipAddress.trim() : "0.0.0.0";
        this.signedAt = signedAt != null ? signedAt : LocalDateTime.now();
    }

    public String getSignedBy() { return signedBy; }
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getSignedAt() { return signedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignatureDetails details = (SignatureDetails) o;
        return Objects.equals(signedBy, details.signedBy) &&
               Objects.equals(ipAddress, details.ipAddress) &&
               Objects.equals(signedAt, details.signedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signedBy, ipAddress, signedAt);
    }
}
