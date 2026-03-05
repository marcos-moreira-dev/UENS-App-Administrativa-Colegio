# Variables de entorno

## App
- `SPRING_PROFILES_ACTIVE`: perfil activo (`dev`, `prod`, etc.).
- `APP_PORT`: puerto host para API en full Docker.

## Base de datos
- `DB_URL`: URL JDBC completa (si existe, tiene prioridad).
- `DB_HOST`: host DB (default local: `localhost`).
- `DB_PORT`: puerto DB (default local compose: `5433`).
- `DB_NAME`: nombre de base.
- `DB_USER`: usuario DB.
- `DB_PASSWORD`: clave DB.

## JWT
- `JWT_SECRET`: secreto para firma.
- `JWT_ISSUER`: emisor esperado.
- `JWT_EXPIRATION_SECONDS`: vigencia en segundos.

## Cola de reportes
- `APP_REPORT_QUEUE_ENABLED`
- `APP_REPORT_QUEUE_INITIAL_DELAY_MS`
- `APP_REPORT_QUEUE_FIXED_DELAY_MS`
- `APP_REPORT_QUEUE_SCHEDULER_POOL_SIZE`
- `APP_REPORT_QUEUE_CLAIM_BATCH_SIZE`
- `APP_REPORT_QUEUE_MAX_ATTEMPTS`

## Generación de archivos de reportes
- `APP_REPORT_OUTPUT_DIR`: directorio de salida para archivos generados (`xlsx`, `pdf`, `docx`).
- `APP_REPORT_PUBLIC_BASE_URL`: base URL para construir links de descarga (opcional).
- `APP_REPORT_FILE_TTL_DAYS`: días de retención de archivos generados.
- `APP_REPORT_MAX_FILE_MB`: tamano máximo permitido por archivo generado.

Nota: si no se setean, se usan defaults de `application.properties`.

