package com.marcosmoreiradev.uensbackend.common.i18n;

public final class MessageKeys {
/**
 * Construye la instancia de MessageKeys para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 */

    private MessageKeys() {
    }

    public static final String API_SUCCESS_DEFAULT = "api.success.default";
    public static final String API_SUCCESS_CREATED = "api.success.created";
    public static final String API_SUCCESS_ACCEPTED = "api.success.accepted";

    public static final String ERROR_VR_01_REQUEST_INVALIDO = "error.vr.01.request_invalido";
    public static final String ERROR_VR_02_PARAMETRO_INVALIDO = "error.vr.02.parametro_invalido";
    public static final String ERROR_VR_03_CUERPO_JSON_INVALIDO = "error.vr.03.cuerpo_json_invalido";
    public static final String ERROR_VR_06_VALOR_ENUM_INVALIDO = "error.vr.06.valor_enum_invalido";
    public static final String ERROR_VR_07_CAMPO_REQUERIDO = "error.vr.07.campo_requerido";

    public static final String ERROR_API_01_REQUEST_MALFORMADO = "error.api.01.request_malformado";
    public static final String ERROR_API_03_TIPO_CONTENIDO_NO_SOPORTADO = "error.api.03.tipo_contenido_no_soportado";
    public static final String ERROR_API_04_RECURSO_NO_ENCONTRADO = "error.api.04.recurso_no_encontrado";
    public static final String ERROR_API_05_METODO_NO_PERMITIDO = "error.api.05.metodo_no_permitido";
    public static final String ERROR_API_06_RUTA_NO_ENCONTRADA = "error.api.06.ruta_no_encontrada";
    public static final String ERROR_API_07_CONFLICTO_OPERACION = "error.api.07.conflicto_operacion";
    public static final String ERROR_API_08_RESPUESTA_NO_ACEPTABLE = "error.api.08.respuesta_no_aceptable";
    public static final String ERROR_API_10_ENDPOINT_EN_CONSTRUCCION = "error.api.10.endpoint_en_construccion";

    public static final String ERROR_AUTH_01_CREDENCIALES_INVALIDAS = "error.auth.01.credenciales_invalidas";
    public static final String ERROR_AUTH_02_TOKEN_INVALIDO = "error.auth.02.token_invalido";
    public static final String ERROR_AUTH_03_TOKEN_EXPIRADO = "error.auth.03.token_expirado";
    public static final String ERROR_AUTH_04_SIN_PERMISOS = "error.auth.04.sin_permisos";
    public static final String ERROR_AUTH_05_USUARIO_INACTIVO = "error.auth.05.usuario_inactivo";
    public static final String ERROR_AUTH_06_LOGIN_TEMPORALMENTE_BLOQUEADO = "error.auth.06.login_temporalmente_bloqueado";
    public static final String ERROR_AUTH_07_RATE_LIMIT_LOGIN_EXCEDIDO = "error.auth.07.rate_limit_login_excedido";
    public static final String ERROR_AUTH_08_REFRESH_TOKEN_INVALIDO = "error.auth.08.refresh_token_invalido";
    public static final String ERROR_AUTH_09_REFRESH_TOKEN_EXPIRADO = "error.auth.09.refresh_token_expirado";
    public static final String ERROR_AUTH_10_ACCESO_DENEGADO = "error.auth.10.acceso_denegado";

    public static final String ERROR_SYS_01_ERROR_INTERNO = "error.sys.01.error_interno";
}
