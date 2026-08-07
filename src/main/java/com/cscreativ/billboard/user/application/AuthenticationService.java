package com.cscreativ.billboard.user.application;

import com.cscreativ.billboard.admin.AdminFacade;
import com.cscreativ.billboard.advertiser.AdvertiserFacade;
import com.cscreativ.billboard.mediabuyer.MediaBuyerFacade;
import com.cscreativ.billboard.owner.OwnerFacade;
import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.domain.exception.InvalidPasswordException;
import com.cscreativ.billboard.user.domain.exception.UserNotFoundException;
import com.cscreativ.billboard.user.domain.repository.UserRepository;
import com.cscreativ.billboard.user.domain.valueobject.Email;
import com.cscreativ.billboard.user.events.UserLoggedInEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Chaque profil métier (annonceur, propriétaire, media buyer, admin) est rattaché à un
 * userId. On les embarque comme claims du JWT à la connexion (via la Facade publique de
 * chaque module, seule API inter-module autorisée par Spring Modulith) pour que le
 * frontend sache, sans appel supplémentaire, à quel(s) espace(s) l'utilisateur a accès.
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;
    private final AdvertiserFacade advertiserFacade;
    private final OwnerFacade ownerFacade;
    private final MediaBuyerFacade mediaBuyerFacade;
    private final AdminFacade adminFacade;

    public AuthenticationService(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder,
                                  JwtService jwtService,
                                  ApplicationEventPublisher eventPublisher,
                                  AdvertiserFacade advertiserFacade,
                                  OwnerFacade ownerFacade,
                                  MediaBuyerFacade mediaBuyerFacade,
                                  AdminFacade adminFacade) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.eventPublisher = eventPublisher;
        this.advertiserFacade = advertiserFacade;
        this.ownerFacade = ownerFacade;
        this.mediaBuyerFacade = mediaBuyerFacade;
        this.adminFacade = adminFacade;
    }

    public String login(String emailStr, String rawPassword) {
        User user = userRepository.findByEmail(new Email(emailStr))
                .orElseThrow(() -> new UserNotFoundException("Identifiants invalides"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword().getHashedValue())) {
            throw new InvalidPasswordException("Identifiants invalides");
        }

        eventPublisher.publishEvent(new UserLoggedInEvent(user.getId(), LocalDateTime.now()));

        Map<String, String> profileClaims = new LinkedHashMap<>();
        advertiserFacade.findAdvertiserIdByUserId(user.getId())
                .ifPresent(id -> profileClaims.put("advertiserId", id.toString()));
        ownerFacade.findOwnerIdByUserId(user.getId())
                .ifPresent(id -> profileClaims.put("ownerId", id.toString()));
        mediaBuyerFacade.findBuyerIdByUserId(user.getId())
                .ifPresent(id -> profileClaims.put("mediaBuyerId", id.toString()));
        adminFacade.findAdminIdByUserId(user.getId())
                .ifPresent(id -> profileClaims.put("adminId", id.toString()));

        return jwtService.generateAccessToken(user.getId(), user.getEmail().getValue(), profileClaims);
    }
}
