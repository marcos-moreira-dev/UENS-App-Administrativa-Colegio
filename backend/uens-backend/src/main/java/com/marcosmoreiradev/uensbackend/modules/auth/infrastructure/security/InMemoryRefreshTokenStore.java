package com.marcosmoreiradev.uensbackend.modules.auth.infrastructure.security;

import com.marcosmoreiradev.uensbackend.modules.auth.application.model.RefreshTokenSession;
import com.marcosmoreiradev.uensbackend.modules.auth.application.port.RefreshTokenStore;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter local en memoria para refresh tokens. Es apropiado para una sola
 * instancia y sirve como simulacion de un store dedicado mas serio.
 */
@Repository
public class InMemoryRefreshTokenStore implements RefreshTokenStore {

    private final ConcurrentHashMap<String, RefreshTokenSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void save(RefreshTokenSession session) {
        sessions.put(session.tokenHash(), session);
    }

    @Override
    public Optional<RefreshTokenSession> findByTokenHash(String tokenHash) {
        return Optional.ofNullable(sessions.get(tokenHash));
    }

    @Override
    public void revoke(String tokenHash) {
        sessions.computeIfPresent(tokenHash, (key, session) -> session.revoke());
    }

    @Override
    public void purgeExpired(Instant now) {
        sessions.entrySet().removeIf(entry -> {
            RefreshTokenSession session = entry.getValue();
            return session.revoked() || !now.isBefore(session.expiresAt());
        });
    }
}
