package com.cscreativ.billboard.user.infrastructure.security;

import com.cscreativ.billboard.shared.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Arrays;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = resolveToken(request);
        if (jwt != null) {
            if (tokenProvider.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(tokenProvider.getEmailFromToken(jwt));
                    AuthenticatedUser principal = new AuthenticatedUser(
                            userDetails.getUsername(),
                            userDetails.getAuthorities(),
                            userDetails.isEnabled(),
                            userDetails.isAccountNonExpired(),
                            userDetails.isCredentialsNonExpired(),
                            userDetails.isAccountNonLocked(),
                            tokenProvider.getUserIdFromToken(jwt),
                            parseUuid(tokenProvider.getClaim(jwt, "advertiserId")),
                            parseUuid(tokenProvider.getClaim(jwt, "ownerId")),
                            parseUuid(tokenProvider.getClaim(jwt, "mediaBuyerId")),
                            parseUuid(tokenProvider.getClaim(jwt, "adminId")),
                            parseRoles(tokenProvider.getClaim(jwt, "adminRoles"))
                    );
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (UsernameNotFoundException e) {
                    SecurityContextHolder.clearContext();
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private static UUID parseUuid(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    private static Set<String> parseRoles(String value) {
        return value == null || value.isBlank()
                ? Set.of()
                : Arrays.stream(value.split(",")).collect(Collectors.toSet());
    }

    /**
     * Le JWT vit dans un cookie HttpOnly (voir JwtTokenProvider.COOKIE_NAME) : le frontend ne le
     * manipule jamais en JS, ce qui inclut le flux SSE de la cloche de notification (EventSource
     * envoie les cookies automatiquement avec withCredentials, plus besoin de le passer en query
     * param où il finirait dans les logs d'accès / l'historique du navigateur).
     * L'en-tête Authorization reste accepté en repli pour les clients non-navigateur (tests, CLI).
     */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return tokenProvider.extractTokenFromCookies(request);
    }
}
