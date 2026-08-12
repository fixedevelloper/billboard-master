package com.cscreativ.billboard.user.application;

import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.domain.UserStatus;
import com.cscreativ.billboard.user.domain.exception.InvalidVerificationTokenException;
import com.cscreativ.billboard.user.domain.repository.UserRepository;
import com.cscreativ.billboard.user.domain.valueobject.Email;
import com.cscreativ.billboard.user.events.UserVerifiedEvent;
import com.cscreativ.billboard.user.events.VerificationEmailRequestedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EmailVerificationService(UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(InvalidVerificationTokenException::new);

        user.confirmVerification(token);
        userRepository.save(user);

        eventPublisher.publishEvent(new UserVerifiedEvent(user.getId(), LocalDateTime.now()));
    }

    /**
     * Ne révèle jamais si l'adresse existe ou non (pas d'exception si l'e-mail est inconnu ou déjà
     * vérifié) : le contrôleur répond systématiquement 204, seul le contenu de la boîte mail
     * change réellement.
     */
    @Transactional
    public void resendVerification(String emailStr) {
        userRepository.findByEmail(new Email(emailStr)).ifPresent(user -> {
            if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
                return;
            }
            String token = user.issueVerificationToken();
            userRepository.save(user);
            eventPublisher.publishEvent(new VerificationEmailRequestedEvent(
                    user.getId(), user.getEmail().getValue(), token, LocalDateTime.now()
            ));
        });
    }
}
