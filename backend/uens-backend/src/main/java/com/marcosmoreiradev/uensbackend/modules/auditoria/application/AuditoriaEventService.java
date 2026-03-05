package com.marcosmoreiradev.uensbackend.modules.auditoria.application;

import com.marcosmoreiradev.uensbackend.common.constants.RequestAttributes;
import com.marcosmoreiradev.uensbackend.modules.auditoria.infrastructure.persistence.entity.AuditoriaEventoJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.auditoria.infrastructure.persistence.repository.AuditoriaEventoJpaRepository;
import com.marcosmoreiradev.uensbackend.security.user.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

@Service
public class AuditoriaEventService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaEventService.class);

    private final AuditoriaEventoJpaRepository repository;

    public AuditoriaEventService(AuditoriaEventoJpaRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarEvento(
            String modulo,
            String accion,
            String entidad,
            String entidadId,
            String resultado,
            String detalle
    ) {
        try {
            SecurityActor actor = resolveActor();
            RequestSnapshot request = resolveRequest();

            AuditoriaEventoJpaEntity entity = AuditoriaEventoJpaEntity.crear(
                    normalize(modulo),
                    normalize(accion),
                    normalizeNullable(entidad),
                    normalizeNullable(entidadId),
                    normalizeResultado(resultado),
                    cleanNullable(detalle),
                    request.requestId(),
                    request.ipOrigen(),
                    actor.userId(),
                    actor.login(),
                    actor.role()
            );
            repository.save(entity);
        } catch (Exception ex) {
            log.warn(
                    "No fue posible persistir evento de auditoria: modulo={}, accion={}, entidad={}, entidadId={}, error={}",
                    modulo,
                    accion,
                    entidad,
                    entidadId,
                    ex.getMessage(),
                    ex
            );
        }
    }

    private static SecurityActor resolveActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return SecurityActor.empty();
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return new SecurityActor(details.getUserId(), details.getUsername(), details.getRole());
        }
        String login = cleanNullable(auth.getName());
        return new SecurityActor(null, login, null);
    }

    private static RequestSnapshot resolveRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes servletAttributes)) {
            return RequestSnapshot.empty();
        }
        HttpServletRequest request = servletAttributes.getRequest();
        String requestId = request.getAttribute(RequestAttributes.REQUEST_ID) instanceof String id ? id : null;
        String forwarded = cleanNullable(request.getHeader("X-Forwarded-For"));
        String remoteIp = forwarded != null ? forwarded.split(",")[0].trim() : cleanNullable(request.getRemoteAddr());
        return new RequestSnapshot(cleanNullable(requestId), cleanNullable(remoteIp));
    }

    private static String normalizeResultado(String value) {
        String normalized = normalize(value);
        return switch (normalized) {
            case "EXITO", "ERROR", "INFO", "ADVERTENCIA" -> normalized;
            default -> "INFO";
        };
    }

    private static String normalize(String value) {
        String cleaned = cleanNullable(value);
        return cleaned == null ? "NO_DEFINIDO" : cleaned.toUpperCase(Locale.ROOT);
    }

    private static String normalizeNullable(String value) {
        String cleaned = cleanNullable(value);
        return cleaned == null ? null : cleaned.toUpperCase(Locale.ROOT);
    }

    private static String cleanNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private record SecurityActor(Long userId, String login, String role) {
        private static SecurityActor empty() {
            return new SecurityActor(null, null, null);
        }
    }

    private record RequestSnapshot(String requestId, String ipOrigen) {
        private static RequestSnapshot empty() {
            return new RequestSnapshot(null, null);
        }
    }
}
