package com.cscreativ.billboard.shared.domain;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime occurredOn();
}
