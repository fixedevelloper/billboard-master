package com.cscreativ.billboard;

import com.cscreativ.billboard.admin.AdminFacade;
import com.cscreativ.billboard.user.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

/**
 * Crée le compte administrateur par défaut au démarrage, à partir des variables
 * d'environnement (ADMIN_EMAIL / ADMIN_PASSWORD / ...) — il n'existe volontairement
 * aucune inscription admin en libre-service côté frontend. Idempotent : si le compte
 * (ou son profil admin) existe déjà, ne fait rien.
 *
 * Vit dans le package racine (pas dans admin/) : il dépend à la fois de UserFacade et
 * AdminFacade, et admin/user se dépendent déjà mutuellement (AuthenticationService lit
 * AdminFacade pour le claim JWT) — le placer dans le module admin créerait un cycle
 * admin -> user -> admin, rejeté par Spring Modulith.
 */
@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountInitializer.class);

    private final UserFacade userFacade;
    private final AdminFacade adminFacade;

    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;

    public AdminAccountInitializer(UserFacade userFacade,
                                    AdminFacade adminFacade,
                                    @Value("${admin.bootstrap.email:}") String email,
                                    @Value("${admin.bootstrap.password:}") String password,
                                    @Value("${admin.bootstrap.first-name:Admin}") String firstName,
                                    @Value("${admin.bootstrap.last-name:Admin}") String lastName) {
        this.userFacade = userFacade;
        this.adminFacade = adminFacade;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank()) {
            log.info("ADMIN_EMAIL / ADMIN_PASSWORD non configurés : pas de bootstrap du compte admin.");
            return;
        }

        UUID userId = userFacade.ensureActiveUser(email, password, firstName, lastName);

        if (adminFacade.findAdminIdByUserId(userId).isPresent()) {
            log.info("Compte admin déjà présent pour {} — rien à faire.", email);
            return;
        }

        adminFacade.createAdmin(userId, Set.of("SUPER_ADMIN"));
        log.info("Compte admin bootstrapé pour {}.", email);
    }
}
