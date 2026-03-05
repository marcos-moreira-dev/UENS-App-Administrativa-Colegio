# 15_backend_v_1_reportes_generacion_archivos_excel_pdf_word

- Versión: 1.0
- Estado: Aprobado para implementacion
- Ámbito: Backend V1 (módulo `reporte`)

---

## 1. Decision cerrada

En V1:

1. El backend genera reportes.
2. La cola `reporte_solicitud_queue` gestiona el procesamiento asíncrono.
3. El cliente solicita, consulta estado y descarga resultado.

Esta decision se apoya en:

- `10_backend_v_1_reporte_solicitudes_cola_simple_db_queue.md`
- `13_backend_v_1_dtos_mappers_matriz_permisos_filtros_y_mapeo_jpa.md`
- implementacion actual del módulo `reporte` en `src/main/java/.../modules/reporte`

---

## 2. Estado actual del código (base real)

Ya existen:

- Cola en BD (`reporte_solicitud_queue`)
- Worker scheduler
- Claim repository
- Selector de processors
- Processors reales:
 - `LISTADO_ESTUDIANTES_POR_SECCION`
 - `CALIFICACIONES_POR_SECCION_Y_PARCIAL`
 - `AUDITORIA_ADMIN_OPERACIONES`
- Strategy de presentacion documental por tipo de reporte (`ReporteDocumentModelAssembler`)
- Strategy de exportación por formato (`ReporteFileExporter`)
- Endpoints:
 - `POST /api/v1/reportes/solicitudes`
 - `GET /api/v1/reportes/solicitudes`
 - `GET /api/v1/reportes/solicitudes/{id}`
 - `GET /api/v1/reportes/solicitudes/{id}/estado`
 - `GET /api/v1/reportes/solicitudes/{id}/resultado`
 - `POST /api/v1/reportes/solicitudes/{id}/reintentar`

---

## 3. Tipos de reporte iniciales (fase de arranque)

Se deben implementar al menos estos 2:

1. `LISTADO_ESTUDIANTES_POR_SECCION`
2. `CALIFICACIONES_POR_SECCION_Y_PARCIAL`
3. `AUDITORIA_ADMIN_OPERACIONES` (solicitado solo desde módulo auditoría por rol ADMIN)

Siguientes recomendados:

1. `RESUMEN_SECCION`
2. `CALIFICACIONES_POR_ESTUDIANTE`

---

## 4. Mini arquitectura técnica (propuesta concreta)

## 4.1 Flujo asíncrono

1. Controller valida request y crea solicitud (`PENDIENTE`).
2. Worker hace claim, pasa a `EN_PROCESO`.
3. `ReporteDataProcessor` genera `resultadoPayload` estructurado.
4. Capa de exportación genera archivo fisico (`xlsx`, `pdf`, `docx`).
5. Solicitud pasa a `COMPLETADA` con metadata de salida.
6. Cliente consulta estado y luego descarga.

## 4.2 Contratos recomendados

- `ReporteDataProcessor`: ya existe, mantiene responsabilidad de preparar datos.
- `ReporteDocumentModelAssembler`:
 - `boolean soporta(String tipoReporte)`
 - `ReporteDocumentModel assemble(ReporteSolicitudQueueJpaEntity solicitud, Object payload)`
- `ReporteDocumentModelAssemblerSelector`: resuelve assembler por tipo.
- `ReporteFileExporter`:
 - `boolean soporta(String formato)`
 - `void exportar(Path outputFile, ReporteDocumentModel documentModel)`
- `ReporteFileExporterSelector`: resuelve exporter por formato.
- `ReporteFileGenerationService`: orquesta assembler + export + persistencia metadata.

Esta separación evita que cada exportador conozca el payload crudo y permite mantener consistencia visual entre `PDF`, `DOCX` y `XLSX`.

## 4.3 Formatos de salida permitidos

- `XLSX`
- `PDF`
- `DOCX`

Valor recomendado para request: `formatoSalida` (`XLSX|PDF|DOCX`).

---

## 5. Persistencia de resultado y archivo

## 5.1 Regla V1 pragmatica

- `resultado_json`: conservar payload normalizado (útil para auditoría y re-render).
- Archivo final: guardar en filesystem local controlado por configuración.
- En BD guardar metadata de archivo dentro de `resultado_json` mientras el esquema siga compacto.

## 5.2 Metadata mínima de archivo

- `nombreArchivo`
- `mimeType`
- `formato`
- `rutaRelativa`
- `tamanoBytes`
- `generadoEn`

---

## 6. Endpoints y contrato funcional

Se mantienen endpoints actuales y se agrega descarga de archivo:

- `GET /api/v1/reportes/solicitudes/{id}/resultado`
 - responde estado + metadata + payload
- `GET /api/v1/reportes/solicitudes/{id}/archivo`
 - devuelve binario segun `mimeType`
 - `404` si no existe
 - `409` si aún no esta `COMPLETADA`
- `POST /api/v1/auditoria/reportes/solicitudes`
 - crea solicitudes de tipo `AUDITORIA_ADMIN_OPERACIONES` (solo `ADMIN`)

Nota: si prefieres no agregar endpoint nuevo en primera iteración, `resultado` puede incluir `downloadUrl` diferido para la siguiente entrega.

---

## 7. Librerias recomendadas (Spring Boot 4 / Java 21)

## 7.1 Excel y Word

- `org.apache.poi:poi-ooxml:5.4.1`

## 7.2 PDF

- opción directa: `org.apache.pdfbox:pdfbox:3.0.4`
- opción HTML->PDF: `com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10`

Recomendación V1: iniciar con `PDFBox` si quieres control programatico y sin pipeline HTML.

---

## 8. Variables de entorno nuevas recomendadas

- `APP_REPORT_OUTPUT_DIR`: directorio raiz de salida de archivos de reporte
- `APP_REPORT_PUBLIC_BASE_URL`: base URL para construir links de descarga (si aplica)
- `APP_REPORT_FILE_TTL_DAYS`: retención de archivos generados
- `APP_REPORT_MAX_FILE_MB`: límite de tamano de salida

---

## 9. Archivos de documentación que deben mantenerse sincronizados

1. `backend/docs/backend_v_1/00_backend_v_1_indice_y_mapa_documental.md`
2. `backend/docs/backend_v_1/10_backend_v_1_reporte_solicitudes_cola_simple_db_queue.md`
3. `backend/docs/backend_v_1/13_backend_v_1_dtos_mappers_matriz_permisos_filtros_y_mapeo_jpa.md`
4. `backend/docs/backend_v_1/15_backend_v_1_reportes_generacion_archivos_excel_pdf_word.md` (este documento)
5. `backend/docs/api/API_ENDPOINTS.md`
6. `backend/docs/despliegue/variables_entorno.md`
7. `backend/docs/despliegue/checklist_release_v1.md`

---

## 10. Orden de implementacion recomendado

1. Mantener sincronizados processors, assemblers y exportadores cuando se agregue un nuevo tipo de reporte.
2. Blindar el `GlobalExceptionHandler` y el worker con pruebas sobre errores de exportación.
3. Mantener probado el endpoint `GET /api/v1/reportes/solicitudes/{id}/archivo` con validación de ruta, MIME y headers de descarga.
4. Actualizar Postman y Swagger con flujo completo.
5. Si crecen los formatos, mover estilos compartidos a una capa de plantillas reutilizable.


