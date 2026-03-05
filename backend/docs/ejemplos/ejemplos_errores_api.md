# Ejemplos de errores API

## Credenciales invalidas
```json
{
  "ok": false,
  "errorCode": "AUTH-01-CREDENCIALES_INVALIDAS",
  "message": "Credenciales invalidas.",
  "details": null,
  "path": "/api/v1/auth/login",
  "requestId": "...",
  "timestamp": "2026-01-10T12:00:00Z"
}
```

## Error de validación
```json
{
  "ok": false,
  "errorCode": "VR-01-REQUEST_INVALIDO",
  "message": "La solicitud contiene errores de validación.",
  "details": [
    {
      "field": "nombres",
      "code": "NotBlank",
      "message": "Los nombres son obligatorios."
    }
  ]
}
```

## Recurso no encontrado
```json
{
  "ok": false,
  "errorCode": "API-04-RECURSO_NO_ENCONTRADO",
  "message": "No se encontro el recurso solicitado."
}
```

## Conflicto de negocio
```json
{
  "ok": false,
  "errorCode": "RN-EST-04-CUPO_SECCION_AGOTADO",
  "message": "La seccion no tiene cupo disponible."
}
```

