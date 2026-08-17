package com.cscreativ.billboard.user.application;

import com.cscreativ.billboard.admin.AdminFacade;
import com.cscreativ.billboard.advertiser.AdvertiserFacade;
import com.cscreativ.billboard.mediabuyer.MediaBuyerFacade;
import com.cscreativ.billboard.owner.OwnerFacade;
import com.cscreativ.billboard.user.domain.Role;
import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.domain.UserStatus;
import com.cscreativ.billboard.user.domain.exception.InvalidPasswordException;
import com.cscreativ.billboard.user.domain.exception.UserDisabledException;
import com.cscreativ.billboard.user.domain.exception.UserNotFoundException;
import com.cscreativ.billboard.user.domain.exception.UserNotVerifiedException;
import com.cscreativ.billboard.user.domain.repository.RoleRepository;
import com.cscreativ.billboard.user.domain.repository.UserRepository;
import com.cscreativ.billboard.user.domain.valueobject.Email;
import com.cscreativ.billboard.user.domain.valueobject.FullName;
import com.cscreativ.billboard.user.domain.valueobject.Password;
import com.cscreativ.billboard.user.events.UserLoggedInEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Chaque profil métier (annonceur, propriétaire, media buyer, admin) est rattaché à un
 * userId. On les embarque comme claims du JWT à la connexion (via la Facade publique de
 * chaque module, seule API inter-module autorisée par Spring Modulith) pour que le
 * frontend sache, sans appel supplémentaire, à quel(s) espace(s) l'utilisateur a accès.
 */
@Service
public class AuthenticationService {

    private static final String DEFAULT_ROLE_NAME = "USER";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;
    private final AdvertiserFacade advertiserFacade;
    private final OwnerFacade ownerFacade;
    private final MediaBuyerFacade mediaBuyerFacade;
    private final AdminFacade adminFacade;

    public AuthenticationService(UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  PasswordEncoder passwordEncoder,
                                  JwtService jwtService,
                                  ApplicationEventPublisher eventPublisher,
                                  AdvertiserFacade advertiserFacade,
                                  OwnerFacade ownerFacade,
                                  MediaBuyerFacade mediaBuyerFacade,
                                  AdminFacade adminFacade) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new UserNotVerifiedException();
        }
        if (user.getStatus() == UserStatus.DISABLED || user.getStatus() == UserStatus.SUSPENDED) {
            throw new UserDisabledException("Votre compte est désactivé. Contactez le support.");
        }

        eventPublisher.publishEvent(new UserLoggedInEvent(user.getId(), LocalDateTime.now()));

        return jwtService.generateAccessToken(user.getId(), user.getEmail().getValue(), buildProfileClaims(user.getId()));
    }

    /**
     * Authentifie via Google/Facebook : rattache l'identité OAuth à un compte existant (retrouvé par
     * providerId puis, à défaut, par email — Google/Facebook ne renvoient que des emails déjà vérifiés
     * par le provider, donc pas de risque de prise de compte) ou en crée un nouveau, actif d'emblée.
     */
    @Transactional
    public String loginOrRegisterViaOAuth(String oauthProvider, String oauthId, String emailStr, String firstName, String lastName) {
        Email email = new Email(emailStr);
        User user = userRepository.findByOauthProviderAndOauthId(oauthProvider, oauthId)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .map(existing -> {
                            existing.linkOAuthIdentity(oauthProvider, oauthId);
                            return userRepository.save(existing);
                        })
                        .orElseGet(() -> registerOAuthUser(email, firstName, lastName, oauthProvider, oauthId)));

        if (user.getStatus() == UserStatus.DISABLED || user.getStatus() == UserStatus.SUSPENDED) {
            throw new UserDisabledException("Votre compte est désactivé. Contactez le support.");
        }

        eventPublisher.publishEvent(new UserLoggedInEvent(user.getId(), LocalDateTime.now()));

        return jwtService.generateAccessToken(user.getId(), user.getEmail().getValue(), buildProfileClaims(user.getId()));
    }

    private User registerOAuthUser(Email email, String firstName, String lastName, String oauthProvider, String oauthId) {
        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new IllegalStateException(
                        "Rôle par défaut '" + DEFAULT_ROLE_NAME + "' introuvable en base"));

        // Jamais communiqué ni utilisable pour se connecter par mot de passe : uniquement là pour
        // satisfaire l'invariant "mot de passe non vide" du VO Password. L'utilisateur peut en
        // définir un plus tard via le flux "mot de passe oublié" existant s'il le souhaite.
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        Password password = new Password(passwordEncoder.encode(Base64.getEncoder().encodeToString(randomBytes)));

        User user = User.createFromOAuth(email, password, new FullName(firstName, lastName), oauthProvider, oauthId, Set.of(defaultRole));
        return userRepository.save(user);
    }

    private Map<String, String> buildProfileClaims(UUID userId) {
        Map<String, String> profileClaims = new LinkedHashMap<>();
        advertiserFacade.findAdvertiserIdByUserId(userId)
                .ifPresent(id -> profileClaims.put("advertiserId", id.toString()));
        ownerFacade.findOwnerIdByUserId(userId)
                .ifPresent(id -> profileClaims.put("ownerId", id.toString()));
        mediaBuyerFacade.findBuyerIdByUserId(userId)
                .ifPresent(id -> profileClaims.put("mediaBuyerId", id.toString()));
        adminFacade.findAdminIdByUserId(userId)
                .ifPresent(id -> profileClaims.put("adminId", id.toString()));
        Set<String> adminRoles = adminFacade.findRoleNamesByUserId(userId);
        if (!adminRoles.isEmpty()) {
            profileClaims.put("adminRoles", String.join(",", adminRoles));
        }
        return profileClaims;
    }
}
