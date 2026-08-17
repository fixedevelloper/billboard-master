package com.cscreativ.billboard.user.infrastructure.security.oauth;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Alternative à {@link org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository},
 * qui refuse une liste vide. Ici la liste peut être vide (aucune credential OAuth configurée) sans
 * empêcher le démarrage de l'application : les endpoints /oauth2/authorization/* renvoient alors 404.
 */
public class MapClientRegistrationRepository implements ClientRegistrationRepository {

    private final Map<String, ClientRegistration> registrations;

    public MapClientRegistrationRepository(List<ClientRegistration> registrations) {
        this.registrations = registrations.stream()
                .collect(Collectors.toMap(ClientRegistration::getRegistrationId, Function.identity()));
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return registrations.get(registrationId);
    }
}
