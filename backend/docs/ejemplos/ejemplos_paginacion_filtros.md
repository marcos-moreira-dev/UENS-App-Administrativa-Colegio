# Ejemplos de paginacion y filtros

## Parametros comunes
- `page`: índice base 0.
- `size`: tamano de pagina.
- `sort`: repetible, formato `campo,direccion`.
- `q`: texto libre (cuando el endpoint lo soporta).

## Estudiantes
```http
GET /api/v1/estudiantes?q=juan&estado=ACTIVO&page=0&size=10&sort=apellidos,asc&sort=nombres,asc
```

## Secciones
```http
GET /api/v1/secciones?estado=ACTIVO&anioLectivo=2025-2026&page=0&size=20&sort=grado,asc
```

## Reportes en cola
```http
GET /api/v1/reportes/solicitudes?estado=PENDIENTE&page=0&size=50&sort=fechaSolicitud,asc
```

## Respuesta esperada
El contenido paginado viene en `data` con estructura `PageResponseDto`:
- `content`
- `page`
- `size`
- `totalElements`
- `totalPages`
- `numberOfElements`
- `first`
- `last`
- `sort`

