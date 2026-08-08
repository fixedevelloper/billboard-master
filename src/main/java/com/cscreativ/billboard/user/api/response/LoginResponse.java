package com.cscreativ.billboard.user.api.response;

import java.util.UUID;

/**
 * Ne contient jamais le JWT : le token vit uniquement dans le cookie HttpOnly posé par
 * AuthController (voir JwtTokenProvider.buildAuthCookie). Ces champs sont de simples
 * identifiants, pas des secrets, donc sans risque à exposer en JSON ou à garder en mémoire
 * côté frontend.
 */
public record LoginResponse(
        UUID userId,
        String email,
        String advertiserId,
        String ownerId,
        String mediaBuyerId,
        String adminId
) {}
