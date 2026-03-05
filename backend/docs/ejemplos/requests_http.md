# Ejemplos HTTP (v1)

## Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "login": "admin",
  "password": "admin123"
}
```

## Perfil autenticado
```http
GET /api/v1/auth/me
Authorization: Bearer <token>
```

## Listar estudiantes
```http
GET /api/v1/estudiantes?page=0&size=20&sort=apellidos,asc
Authorization: Bearer <token>
```

## Crear estudiante
```http
POST /api/v1/estudiantes
Authorization: Bearer <token>
Content-Type: application/json

{
  "nombres": "Juan",
  "apellidos": "Perez",
  "fechaNacimiento": "2015-01-10",
  "representanteLegalId": 10,
  "seccionId": 3
}
```

## Crear solicitud de reporte
```http
POST /api/v1/reportes/solicitudes
Authorization: Bearer <token>
Content-Type: application/json

{
  "tipoReporte": "LISTADO_ESTUDIANTES_POR_SECCION",
  "seccionId": 3
}
```
