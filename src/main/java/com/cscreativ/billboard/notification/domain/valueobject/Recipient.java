package com.cscreativ.billboard.notification.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class Recipient {
    private final UUID recipientId;
    private final String destination; // Email, phone number, device token, etc.

    public Recipient(UUID recipientId, String destination) {
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("La destination du destinataire ne peut pas être vide");
        }
        this.recipientId = recipientId;
        this.destination = destination.trim();
    }

    public UUID getRecipientId() { return recipientId; }
    public String getDestination() { return destination; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipient recipient = (Recipient) o;
        return Objects.equals(recipientId, recipient.recipientId) &&
               Objects.equals(destination, recipient.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipientId, destination);
    }
}
