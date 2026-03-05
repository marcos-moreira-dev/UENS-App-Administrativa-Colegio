package com.marcosmoreiradev.uensbackend.security.jwt;

import com.marcosmoreiradev.uensbackend.common.constants.SecurityRoles;
import com.marcosmoreiradev.uensbackend.config.properties.JwtProperties;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
/**
 * Define la responsabilidad de JwtTokenService dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: orquestar comportamiento de aplicacion entre dominio, persistencia y seguridad.
 */
public class JwtTokenService {

    private static final Base64.Encoder B64_URL_ENC = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64_URL_DEC = Base64.getUrlDecoder();
/**
 * Metodo de soporte interno 'operacion' para mantener cohesion en JwtTokenService.
 * Contexto: modulo core, capa security, con foco en reglas y consistencia tecnica de fase 1.
 */
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final JwtProperties props;
    private final ObjectMapper objectMapper;

/**
 * Construye la instancia de JwtTokenService para operar en el modulo core.
 * Contexto: capa security con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param props propiedades de configuracion inyectadas para parametrizar comportamiento
     * @param objectMapper serializador JSON usado para claims, payloads o campos JSONB
 */
    public JwtTokenService(JwtProperties props, ObjectMapper objectMapper) {
        this.props = props;
        this.objectMapper = objectMapper;
    }

/**
 * Implementa la operacion 'generateToken' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param subject claim sub del JWT que representa la identidad autenticada
     * @param role rol funcional del usuario (ADMIN o SECRETARIA) usado para autorizacion
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String generateToken(String subject, String role) {
        try {
            String secret = requireNonBlank(props.secret(), "JWT secret no configurado.");
            long expirationSeconds = props.expirationSeconds();
            if (expirationSeconds <= 0) {
                throw new IllegalStateException("JWT expirationSeconds debe ser > 0.");
            }
            if (!SecurityRoles.isSupported(role)) {
                throw new IllegalArgumentException("Rol no soportado para JWT.");
            }

            Instant now = Instant.now();
            Instant exp = now.plusSeconds(expirationSeconds);

            Map<String, Object> header = Map.of(
                    "alg", "HS256",
                    "typ", "JWT"
            );

            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", requireNonBlank(subject, "JWT subject no puede ser vacio."));
            payload.put("rol", role);
            payload.put("iat", now.getEpochSecond());
            payload.put("exp", exp.getEpochSecond());

            String issuer = props.issuer();
            if (issuer != null && !issuer.isBlank()) {
                payload.put("iss", issuer);
            }

            String headerB64 = base64UrlJson(header);
            String payloadB64 = base64UrlJson(payload);
            String signingInput = headerB64 + "." + payloadB64;
            String signatureB64 = signHs256(signingInput, secret);

            return signingInput + "." + signatureB64;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar JWT.", e);
        }
    }

/**
 * Implementa la operacion 'parseAndValidate' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param token token JWT recibido desde cabecera Authorization para validacion de seguridad
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
     * @throws JwtInvalidException si el token JWT no cumple formato, firma o estructura esperada.
     * @throws JwtExpiredException si el token JWT ya supero su fecha de expiracion.
 */
    public JwtPrincipal parseAndValidate(String token) throws JwtInvalidException, JwtExpiredException {
        try {
            if (token == null || token.isBlank()) {
                throw new JwtInvalidException("JWT vacio.");
            }

            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new JwtInvalidException("Formato JWT invalido.");
            }

            String secret = requireNonBlank(props.secret(), "JWT secret no configurado.");
            String signingInput = parts[0] + "." + parts[1];

            byte[] expectedSig = B64_URL_DEC.decode(signHs256(signingInput, secret));
            byte[] actualSig = B64_URL_DEC.decode(parts[2]);
            if (!MessageDigest.isEqual(expectedSig, actualSig)) {
                throw new JwtInvalidException("Firma JWT invalida.");
            }

            byte[] payloadBytes = B64_URL_DEC.decode(parts[1]);
            Map<String, Object> payload = objectMapper.readValue(payloadBytes, MAP_TYPE);

            long iat = requireLong(payload.get("iat"), "JWT sin iat.");
            long exp = requireLong(payload.get("exp"), "JWT sin exp.");
            long now = Instant.now().getEpochSecond();
            if (now >= exp) {
                throw new JwtExpiredException("JWT expirado.");
            }

            String expectedIssuer = props.issuer();
            if (expectedIssuer != null && !expectedIssuer.isBlank()) {
                String tokenIssuer = asString(payload.get("iss"));
                if (!expectedIssuer.equals(tokenIssuer)) {
                    throw new JwtInvalidException("Issuer JWT invalido.");
                }
            }

            String sub = requireNonBlank(asString(payload.get("sub")), "JWT sin sub.");
            String role = requireNonBlank(asString(payload.get("rol")), "JWT sin rol.");
            if (!SecurityRoles.isSupported(role)) {
                throw new JwtInvalidException("Rol JWT no soportado.");
            }

            return new JwtPrincipal(sub, role, Instant.ofEpochSecond(iat), Instant.ofEpochSecond(exp));
        } catch (JwtInvalidException | JwtExpiredException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtInvalidException("JWT invalido.", e);
        }
    }

/**
 * Metodo de soporte interno 'base64UrlJson' para mantener cohesion en JwtTokenService.
 * Contexto: modulo core, capa security, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param obj objeto a serializar o transformar para persistencia/transferencia
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
     * @throws Exception si ocurre un error tecnico no controlado durante esta operacion.
 */
    private String base64UrlJson(Object obj) throws Exception {
        byte[] json = objectMapper.writeValueAsBytes(obj);
        return B64_URL_ENC.encodeToString(json);
    }

/**
 * Metodo de soporte interno 'signHs256' para mantener cohesion en JwtTokenService.
 * Contexto: modulo core, capa security, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param signingInput segmento header.payload del JWT usado para calcular firma HMAC
     * @param secret clave secreta HMAC usada para firmar o verificar tokens JWT
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
     * @throws Exception si ocurre un error tecnico no controlado durante esta operacion.
 */
    private String signHs256(String signingInput, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] sig = mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
        return B64_URL_ENC.encodeToString(sig);
    }

/**
 * Metodo de soporte interno 'requireLong' para mantener cohesion en JwtTokenService.
 * Contexto: modulo core, capa security, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @param messageIfMissing mensaje de error aplicado cuando el dato obligatorio no esta presente
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
     * @throws JwtInvalidException si el token JWT no cumple formato, firma o estructura esperada.
 */
    private long requireLong(Object value, String messageIfMissing) throws JwtInvalidException {
        if (value == null) {
            throw new JwtInvalidException(messageIfMissing);
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            throw new JwtInvalidException("Claim numerico invalido.");
        }
    }

/**
 * Metodo de soporte interno 'asString' para mantener cohesion en JwtTokenService.
 * Contexto: modulo core, capa security, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

/**
 * Metodo de soporte interno 'requireNonBlank' para mantener cohesion en JwtTokenService.
 * Contexto: modulo core, capa security, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}

