# Checklist release v1

## Código
- [ ] `mvn -DskipTests=false test` en verde.
- [ ] `mvn -DskipTests package` en verde.
- [ ] Sin TODO críticos pendientes.
- [ ] Sin secretos hardcodeados.

## API y contrato
- [ ] Endpoints clave validados (auth, estudiantes, reportes).
- [ ] Códigos de error estables documentados.
- [ ] Swagger accesible en entorno release.
- [ ] Flujo asíncrono de reportes validado (`PENDIENTE -> EN_PROCESO -> COMPLETADA/ERROR`).
- [ ] Descarga de archivo de reporte validada (si ya aplica en esta release).

## Datos y migraciones
- [ ] Esquema DB v2 aplicado en ambiente objetivo.
- [ ] Backups previos disponibles.
- [ ] Variables de conexión verificadas.

## Seguridad
- [ ] `JWT_SECRET` fuerte en ambiente release.
- [ ] CORS y puertos revisados.
- [ ] Usuarios de DB con permisos mínimos.

## Docker/ops
- [ ] Imagen construye sin errores.
- [ ] `docker compose --profile full up` funcional.
- [ ] Healthcheck del servicio DB estable.
- [ ] Logs sin errores de arranque.
- [ ] Directorio de salida de reportes con permisos correctos para escritura/lectura.

## Documentación
- [ ] README actualizado.
- [ ] ADRs sincronizados con implementación.
- [ ] Variables de entorno actualizadas.
- [ ] `backend/docs/backend_v_1/15_backend_v_1_reportes_generacion_archivos_excel_pdf_word.md` sincronizado.


