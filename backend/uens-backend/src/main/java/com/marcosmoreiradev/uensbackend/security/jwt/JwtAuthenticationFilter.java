package com.marcosmoreiradev.uensbackend.security.jwt;

import com.marcosmoreiradev.uensbackend.common.constants.ApiHeaders;
import com.marcosmoreiradev.uensbackend.common.constants.SecurityRoles;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.security.handler.RestAuthenticationEntryPoint;
import com.marcosmoreiradev.uensbackend.security.user.CustomUserDetails;
import com.marcosmoreiradev.uensbackend.security.user.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
/**
 * Define la responsabilidad de JwtAuthenticationFilter dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: interceptar flujo HTTP para aplicar controles transversales de seguridad o request handling.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenService;
    private final CustomUserDetailsService userDetailsService;
    private final RestAuthenticationEntryPoint entryPoint;
/**
 * Construye la instancia de JwtAuthenticationFilter para operar en el modulo core.
 * Contexto: capa security con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param tokenService dato de entrada relevante para ejecutar esta operacion: 'tokenService'
     * @param userDetailsService servicio para reconstruir principal autenticado desde la BD
     * @param entryPoint componente que responde 401 cuando la autenticacion falla
 */

    public JwtAuthenticationFilter(
            JwtTokenService tokenService,
            CustomUserDetailsService userDetailsService,
            RestAuthenticationEntryPoint entryPoint
    ) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.entryPoint = entryPoint;
    }

    @Override
/**
 * Implementa la operacion 'shouldNotFilter' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
     * @throws ServletException si se presenta una condicion excepcional de tipo ServletException en la capa security.
 */
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        if (path == null || path.isBlank()) {
            path = request.getRequestURI();
        }

        boolean isH2ConsolePath = path != null && (path.equals("/h2-console") || path.startsWith("/h2-console/"));
        boolean isSwaggerPath = path != null && (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs"));
        boolean isLoginEndpoint = HttpMethod.POST.matches(request.getMethod()) && "/api/v1/auth/login".equals(path);
        boolean isPublicHealth = HttpMethod.GET.matches(request.getMethod())
                && ("/actuator/health".equals(path) || "/actuator/info".equals(path));

        return isH2ConsolePath || isSwaggerPath || isLoginEndpoint || isPublicHealth;
    }
/**
 * Implementa la operacion 'doFilterInternal' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @param response respuesta HTTP sobre la que se escriben cabeceras o cuerpo de error
     * @param filterChain dato de entrada relevante para ejecutar esta operacion: 'filterChain'
     * @throws ServletException si el flujo no puede completarse con las condiciones vigentes.
     * @throws IOException si el flujo no puede completarse con las condiciones vigentes.
 */

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String auth = request.getHeader(ApiHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith(ApiHeaders.BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = auth.substring(ApiHeaders.BEARER_PREFIX.length()).trim();

        try {
            JwtPrincipal principal = tokenService.parseAndValidate(token);
            String role = principal.role();
            if (!SecurityRoles.isSupported(role)) {
                throw new JwtInvalidException("Rol JWT no soportado.");
            }

            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(principal.subject());
            if (!role.equalsIgnoreCase(userDetails.getRole())) {
                throw new JwtInvalidException("Rol JWT no coincide con usuario actual.");
            }

            var authentication = new JwtUserAuthenticationToken(
                    userDetails,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JwtExpiredException e) {
            request.setAttribute(RestAuthenticationEntryPoint.ATTR_AUTH_ERROR_CODE, AuthErrorCodes.AUTH_03_TOKEN_EXPIRADO.code());
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, new InsufficientAuthenticationException("Token expirado."));
        } catch (JwtInvalidException e) {
            request.setAttribute(RestAuthenticationEntryPoint.ATTR_AUTH_ERROR_CODE, AuthErrorCodes.AUTH_02_TOKEN_INVALIDO.code());
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, new InsufficientAuthenticationException("Token invalido."));
        }
    }
}
