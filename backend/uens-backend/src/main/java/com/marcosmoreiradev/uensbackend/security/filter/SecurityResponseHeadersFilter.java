package com.marcosmoreiradev.uensbackend.security.filter;

import com.marcosmoreiradev.uensbackend.config.properties.SecurityHeadersProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Aplica encabezados defensivos transversales para reducir superficie de ataque
 * en clientes basados en navegador y mantener una postura segura por defecto.
 */
@Component
public class SecurityResponseHeadersFilter extends OncePerRequestFilter {

    private static final String HEADER_X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    private static final String HEADER_X_FRAME_OPTIONS = "X-Frame-Options";
    private static final String HEADER_REFERRER_POLICY = "Referrer-Policy";
    private static final String HEADER_PERMISSIONS_POLICY = "Permissions-Policy";
    private static final String HEADER_STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";

    private final SecurityHeadersProperties properties;

    public SecurityResponseHeadersFilter(SecurityHeadersProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        writeIfAbsent(response, HEADER_X_CONTENT_TYPE_OPTIONS, "nosniff");
        writeIfAbsent(response, HEADER_X_FRAME_OPTIONS, "DENY");
        writeIfAbsent(response, HEADER_REFERRER_POLICY, properties.referrerPolicy());
        writeIfAbsent(response, HEADER_PERMISSIONS_POLICY, properties.permissionsPolicy());

        if (properties.hstsEnabled() && request.isSecure()) {
            String hstsValue = "max-age=" + properties.hstsMaxAgeSeconds();
            if (properties.hstsIncludeSubdomains()) {
                hstsValue = hstsValue + "; includeSubDomains";
            }
            writeIfAbsent(response, HEADER_STRICT_TRANSPORT_SECURITY, hstsValue);
        }
    }

    private static void writeIfAbsent(HttpServletResponse response, String headerName, String value) {
        if (!response.containsHeader(headerName)) {
            response.setHeader(headerName, value);
        }
    }
}
