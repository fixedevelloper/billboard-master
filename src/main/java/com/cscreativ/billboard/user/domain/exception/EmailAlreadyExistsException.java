package com.cscreativ.billboard.user.domain.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Un utilisateur existe déjà avec l'adresse email : " + email);
    }
}
