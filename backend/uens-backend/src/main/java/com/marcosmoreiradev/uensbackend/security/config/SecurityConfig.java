package com.marcosmoreiradev.uensbackend.security.config;

import com.marcosmoreiradev.uensbackend.common.constants.SecurityRoles;
import com.marcosmoreiradev.uensbackend.security.filter.SecurityResponseHeadersFilter;
import com.marcosmoreiradev.uensbackend.security.handler.RestAccessDeniedHandler;
import com.marcosmoreiradev.uensbackend.security.handler.RestAuthenticationEntryPoint;
import com.marcosmoreiradev.uensbackend.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity

/**
 * Define la responsabilidad de SecurityConfig dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: centralizar configuracion transversal del contexto Spring Boot.
 */
public class SecurityConfig {

    @Bean
    @Profile("dev")
    @Order(1)
/**
 * Implementa la operacion 'h2ConsoleSecurityFilterChain' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param http dato de entrada relevante para ejecutar esta operacion: 'http'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
     * @throws Exception si ocurre un error tecnico no controlado durante esta operacion.
 */
    public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/h2-console/**")
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
/**
 * Implementa la operacion 'securityFilterChain' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param http dato de entrada relevante para ejecutar esta operacion: 'http'
     * @param jwtFilter dato de entrada relevante para ejecutar esta operacion: 'jwtFilter'
     * @param entryPoint componente que responde 401 cuando la autenticacion falla
     * @param accessDeniedHandler dato de entrada relevante para ejecutar esta operacion: 'accessDeniedHandler'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
     * @throws Exception si ocurre un error tecnico no controlado durante esta operacion.
 */

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter,
            ApiCorsConfigurationSourceFactory corsConfigurationSourceFactory,
            SecurityResponseHeadersFilter securityResponseHeadersFilter,
            RestAuthenticationEntryPoint entryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSourceFactory.create()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/system/ping").permitAll()

                        // Base protected endpoint
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/me")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARIA)

                        // Default policy
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(securityResponseHeadersFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
/**
 * Implementa la operacion 'passwordEncoder' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
