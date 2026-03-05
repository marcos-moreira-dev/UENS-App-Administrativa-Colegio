package com.marcosmoreiradev.uensdesktop.ui.tooltip;

import java.util.Map;
import java.util.Optional;

public final class TooltipCatalog {

    private static final Map<String, String> BUTTON_HINTS = Map.ofEntries(
            Map.entry("Ver", "Abre el detalle completo para revisar datos antes de decidir cambios, estados o descarga."),
            Map.entry("Editar", "Abre el formulario para corregir la informaci\u00f3n operativa del registro seleccionado."),
            Map.entry("Estado", "Consulta el avance actual del proceso o revisa si la solicitud ya puede descargarse."),
            Map.entry("Consultar estado", "Consulta el avance actual del proceso o revisa si la solicitud ya puede descargarse."),
            Map.entry("Inactivar", "Saca el registro de la operaci\u00f3n diaria sin borrar su trazabilidad administrativa."),
            Map.entry("Activar", "Devuelve el registro a la operaci\u00f3n diaria para que vuelva a estar disponible."),
            Map.entry("Buscar", "Aplica el recorte actual para localizar registros, evidencias o solicitudes relevantes."),
            Map.entry("Aplicar", "Ejecuta los filtros definidos y refresca el conjunto de trabajo visible."),
            Map.entry("Limpiar", "Restablece los filtros de la pantalla y vuelve al escenario general de revisi\u00f3n."),
            Map.entry("Anterior", "Retrocede una p\u00e1gina para revisar registros anteriores del mismo listado."),
            Map.entry("Siguiente", "Avanza una p\u00e1gina para continuar la revisi\u00f3n del listado."),
            Map.entry("Cerrar", "Cierra este panel de trabajo y vuelve al contexto principal sin perder la vista actual."),
            Map.entry("Guardar", "Persiste en backend los datos capturados para dejar la operaci\u00f3n registrada."),
            Map.entry("Descargar", "Baja el archivo generado por el backend para entrega, archivo o evidencia administrativa."),
            Map.entry("Reintentar", "Vuelve a poner en curso una operaci\u00f3n fallida para recuperar el proceso administrativo."),
            Map.entry("Refrescar", "Consulta de nuevo el backend para traer el estado m\u00e1s reciente del registro."),
            Map.entry("Copiar", "Copia este dato t\u00e9cnico para soporte, auditor\u00eda o seguimiento."),
            Map.entry("Asignar seccion", "Relaciona al estudiante con su secci\u00f3n vigente para sostener matr\u00edcula y calificaciones."),
            Map.entry("Asignar secci\u00f3n", "Relaciona al estudiante con su secci\u00f3n vigente para sostener matr\u00edcula y calificaciones."),
            Map.entry("Solicitar reporte", "Crea una solicitud as\u00edncrona para que el backend prepare evidencia descargable."));

    private static final Map<String, String> PROMPT_HINTS = Map.ofEntries(
            Map.entry("Busqueda general", "Busca por requestId, entidad afectada, acci\u00f3n registrada o actor que intervino."),
            Map.entry("B\u00fasqueda general", "Busca por requestId, entidad afectada, acci\u00f3n registrada o actor que intervino."),
            Map.entry("Selecciona el tipo", "Elige el reporte o cat\u00e1logo operativo que necesitas generar o consultar."),
            Map.entry("Selecciona el formato", "Define el formato final del archivo que recibir\u00e1 administraci\u00f3n."),
            Map.entry("Selecciona una seccion", "Selecciona la secci\u00f3n acad\u00e9mica que agrupa estudiantes, clases y calificaciones."),
            Map.entry("Selecciona una secci\u00f3n", "Selecciona la secci\u00f3n acad\u00e9mica que agrupa estudiantes, clases y calificaciones."),
            Map.entry("Selecciona una asignatura", "Selecciona la asignatura del cat\u00e1logo curricular que formar\u00e1 parte de la operaci\u00f3n."),
            Map.entry("Actor login", "Filtra por el usuario del sistema que ejecut\u00f3 la acci\u00f3n administrativa."),
            Map.entry("Accion", "Acota por el nombre t\u00e9cnico o funcional de la acci\u00f3n que deseas rastrear."),
            Map.entry("Acci\u00f3n", "Acota por el nombre t\u00e9cnico o funcional de la acci\u00f3n que deseas rastrear."),
            Map.entry("Paralelo", "Indica el paralelo de la secci\u00f3n para distinguir grupos del mismo grado."),
            Map.entry("Anio lectivo", "Filtra por el per\u00edodo acad\u00e9mico en el que opera la secci\u00f3n o el registro."),
            Map.entry("A\u00f1o lectivo", "Filtra por el per\u00edodo acad\u00e9mico en el que opera la secci\u00f3n o el registro."),
            Map.entry("Usuario", "Ingresa el login con el que operar\u00e1s el sistema administrativo."),
            Map.entry("Contrasena", "Ingresa la credencial asociada al usuario para abrir la sesi\u00f3n segura."),
            Map.entry("Contrase\u00f1a", "Ingresa la credencial asociada al usuario para abrir la sesi\u00f3n segura."));

    private TooltipCatalog() {
    }

    public static Optional<String> forButtonText(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(BUTTON_HINTS.get(text.trim()));
    }

    public static Optional<String> forPrompt(String promptText) {
        if (promptText == null || promptText.isBlank()) {
            return Optional.empty();
        }
        String normalizedPrompt = promptText.trim();
        String hint = PROMPT_HINTS.get(normalizedPrompt);
        if (hint != null && !hint.isBlank()) {
            return Optional.of(hint);
        }
        return Optional.of("Completa este campo para delimitar mejor la operaci\u00f3n que vas a ejecutar.");
    }
}
