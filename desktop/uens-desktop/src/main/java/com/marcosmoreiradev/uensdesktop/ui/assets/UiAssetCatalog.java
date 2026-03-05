package com.marcosmoreiradev.uensdesktop.ui.assets;

import com.marcosmoreiradev.uensdesktop.nav.ViewId;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class UiAssetCatalog {

    private static final Map<UiAssetId, String> RESOURCE_PATHS = new EnumMap<>(UiAssetId.class);
    private static final Map<ViewId, UiAssetId> VIEW_ARTWORK = new EnumMap<>(ViewId.class);

    static {
        RESOURCE_PATHS.put(UiAssetId.BRAND_LOGO, "/assets/pictures/big/logo.png");
        RESOURCE_PATHS.put(UiAssetId.BRAND_ICON, "/assets/pictures/big/logoIcon.png");
        RESOURCE_PATHS.put(UiAssetId.INFORMATION_ICON, "/assets/pictures/big/information.png");
        RESOURCE_PATHS.put(UiAssetId.LOGOUT_TICK, "/assets/sounds/tick.mp3");
        RESOURCE_PATHS.put(UiAssetId.LOGIN_CONTEXT, "/assets/pictures/big/Usuario sistema administrativo.png");
        RESOURCE_PATHS.put(UiAssetId.DASHBOARD_CONTEXT, "/assets/pictures/big/logo.png");
        RESOURCE_PATHS.put(UiAssetId.ESTUDIANTES_CONTEXT, "/assets/pictures/big/Estudiante.png");
        RESOURCE_PATHS.put(UiAssetId.REPRESENTANTES_CONTEXT, "/assets/pictures/big/Representante legal.png");
        RESOURCE_PATHS.put(UiAssetId.DOCENTES_CONTEXT, "/assets/pictures/big/Docente.png");
        RESOURCE_PATHS.put(UiAssetId.SECCIONES_CONTEXT, "/assets/pictures/big/Secci\u00f3n.png");
        RESOURCE_PATHS.put(UiAssetId.ASIGNATURAS_CONTEXT, "/assets/pictures/big/Asignatura.png");
        RESOURCE_PATHS.put(UiAssetId.CLASES_CONTEXT, "/assets/pictures/big/Clase.png");
        RESOURCE_PATHS.put(UiAssetId.CALIFICACIONES_CONTEXT, "/assets/pictures/big/Calificaci\u00f3n.png");
        RESOURCE_PATHS.put(UiAssetId.REPORTES_CONTEXT, "/assets/pictures/big/Reporte.png");
        RESOURCE_PATHS.put(UiAssetId.AUDITORIA_CONTEXT, "/assets/pictures/big/Auditor\u00eda.png");

        VIEW_ARTWORK.put(ViewId.LOGIN, UiAssetId.LOGIN_CONTEXT);
        VIEW_ARTWORK.put(ViewId.DASHBOARD, UiAssetId.DASHBOARD_CONTEXT);
        VIEW_ARTWORK.put(ViewId.ESTUDIANTES, UiAssetId.ESTUDIANTES_CONTEXT);
        VIEW_ARTWORK.put(ViewId.REPRESENTANTES, UiAssetId.REPRESENTANTES_CONTEXT);
        VIEW_ARTWORK.put(ViewId.DOCENTES, UiAssetId.DOCENTES_CONTEXT);
        VIEW_ARTWORK.put(ViewId.SECCIONES, UiAssetId.SECCIONES_CONTEXT);
        VIEW_ARTWORK.put(ViewId.ASIGNATURAS, UiAssetId.ASIGNATURAS_CONTEXT);
        VIEW_ARTWORK.put(ViewId.CLASES, UiAssetId.CLASES_CONTEXT);
        VIEW_ARTWORK.put(ViewId.CALIFICACIONES, UiAssetId.CALIFICACIONES_CONTEXT);
        VIEW_ARTWORK.put(ViewId.REPORTES, UiAssetId.REPORTES_CONTEXT);
        VIEW_ARTWORK.put(ViewId.AUDITORIA, UiAssetId.AUDITORIA_CONTEXT);
    }

    private UiAssetCatalog() {
    }

    public static Optional<String> resourcePath(UiAssetId assetId) {
        return Optional.ofNullable(RESOURCE_PATHS.get(assetId));
    }

    public static UiAssetId artworkFor(ViewId viewId) {
        return VIEW_ARTWORK.getOrDefault(viewId, UiAssetId.DASHBOARD_CONTEXT);
    }
}
