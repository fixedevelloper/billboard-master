package com.cscreativ.billboard.shared;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Principal enrichi avec les identifiants de profil (advertiserId/ownerId/mediaBuyerId/adminId)
 * déjà présents comme claims du JWT (voir AuthenticationService.login). Sans ça, un contrôleur
 * n'a aucun moyen de savoir "qui appelle" au-delà de son email : c'est cette lacune qui rendait
 * possible l'IDOR généralisé (n'importe quel id de ressource accepté sans vérifier qu'il
 * correspond bien à l'appelant).
 *
 * Vit dans le package racine du module "user" (comme UserFacade) : c'est la partie exposée du
 * module au sens Spring Modulith, seule utilisable par les autres modules (voir ModularityTests).
 */
public class AuthenticatedUser extends User {

    private final UUID userId;
    private final UUID advertiserId;
    private final UUID ownerId;
    private final UUID mediaBuyerId;
    private final UUID adminId;
    private final Set<String> adminRoles;

    public AuthenticatedUser(String email,
                              Collection<? extends GrantedAuthority> authorities,
                              boolean enabled,
                              boolean accountNonExpired,
                              boolean credentialsNonExpired,
                              boolean accountNonLocked,
                              UUID userId,
                              UUID advertiserId,
                              UUID ownerId,
                              UUID mediaBuyerId,
                              UUID adminId,
                              Set<String> adminRoles) {
        super(email, "", enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.advertiserId = advertiserId;
        this.ownerId = ownerId;
        this.mediaBuyerId = mediaBuyerId;
        this.adminId = adminId;
        this.adminRoles = adminRoles != null ? adminRoles : Set.of();
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getAdvertiserId() {
        return advertiserId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public UUID getMediaBuyerId() {
        return mediaBuyerId;
    }

    public UUID getAdminId() {
        return adminId;
    }

    public boolean isAdmin() {
        return adminId != null;
    }

    /** Noms de rôle bruts (ex. "SUPER_ADMIN") : voir AdminFacade#findRoleNamesByUserId. */
    public boolean hasAdminRole(String roleName) {
        return adminId != null && adminRoles.contains(roleName);
    }

    public boolean isSelf(UUID otherUserId) {
        return otherUserId != null && otherUserId.equals(userId);
    }

    public boolean isAdvertiser(UUID id) {
        return id != null && id.equals(advertiserId);
    }

    public boolean isOwner(UUID id) {
        return id != null && id.equals(ownerId);
    }

    public boolean isMediaBuyer(UUID id) {
        return id != null && id.equals(mediaBuyerId);
    }

    /** Vrai si l'un des identifiants connus de l'appelant (user/advertiser/owner/mediaBuyer) vaut id. */
    public boolean isOneOfMine(UUID id) {
        return isSelf(id) || isAdvertiser(id) || isOwner(id) || isMediaBuyer(id);
    }

    public void require(boolean authorized) {
        if (!authorized) {
            throw new AccessDeniedException("Action non autorisée");
        }
    }

    public void requireAdmin() {
        require(isAdmin());
    }

    public void requireAdminRole(String roleName) {
        require(hasAdminRole(roleName));
    }

    public void requireSelfOrAdmin(UUID otherUserId) {
        require(isAdmin() || isSelf(otherUserId));
    }
}
