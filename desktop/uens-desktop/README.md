# UENS Desktop

Cliente desktop JavaFX del sistema UENS.

Este módulo contiene la aplicación de escritorio y el flujo de empaquetado para distribuirla en Windows.

## Comandos base (desarrollo)

```powershell
..\..\mvnw.cmd -pl desktop/uens-desktop javafx:run
..\..\mvnw.cmd -pl desktop/uens-desktop test
```

## Distribución Windows (MSI / app-image)

El script de distribución esta en:

- `tools/scripts/dist/build-installer.ps1`

Este script automatiza:

1. `clean package` del módulo desktop
2. copia de dependencias runtime al input de `jpackage`
3. generación de paquete `msi` o `app-image`
4. uso del icono oficial del proyecto

## Prerrequisitos

- JDK 21+ con `jpackage` disponible en `PATH`
- WiX Toolset v3.x (`candle.exe` y `light.exe`) para generar `msi`

Nota importante:

- El script intenta autodetectar rutas comunes de WiX, aunque no este en `PATH`.
- Si tu politica de PowerShell bloquea scripts, ejecuta el comando con `-ExecutionPolicy Bypass`.

## Generar MSI (instalador)

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\scripts\dist\build-installer.ps1 -PackageType msi -AppVersion 1.0.0
```

Salida esperada:

- `target/installer/UENS-Desktop-1.0.0.msi`

## Personalizacion visual del instalador MSI

El script soporta banner e imagen de dialogo para el asistente MSI de forma automatica.

Ubica tus archivos en:

- `src/main/resources/assets/pictures/installer images/banner.bmp`
- `src/main/resources/assets/pictures/installer images/dialog.bmp`

Si ambos archivos existen, el script los aplica al wizard MSI sin flags extra.

### Tamano recomendado de imagenes

- `banner.bmp`: `493x58` (franja superior del wizard)
- `dialog.bmp`: `493x312` (panel lateral/fondo del wizard)

Recomendaciones prácticas:

- Genera con IA en resolucion 4x y luego reduce para mejor nitidez.
- Usa formato BMP 24-bit sin compresion.
- Evita texto pequeno en `banner.bmp`; es una franja delgada.

## Generar app-image (portable, sin instalador MSI)

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\scripts\dist\build-installer.ps1 -PackageType app-image -AppVersion 1.0.0
```

Salida esperada:

- `target/installer/UENS-Desktop/`

## Desinstalador: si, viene incluido en MSI

Cuando instalas por `msi` (Windows Installer), la desinstalacion queda integrada automaticamente.

Puedes desinstalar desde:

- `Configuracion > Aplicaciones > Aplicaciones instaladas`
- `Panel de control > Programas y caracteristicas`

No necesitas escribir un desinstalador manual para este caso.

## Diferencia práctica: MSI vs app-image

- `msi`:
 - instala en el sistema
 - registra acceso en menu/inicio segun flags
 - incluye flujo de desinstalacion del sistema
- `app-image`:
 - carpeta portable
 - no registra instalacion formal en Windows Installer
 - no aparece como app instalada para desinstalar

## Troubleshooting rápido

- Error: `No se encontro jpackage`
 - Verifica que estas usando JDK 21+ y no solo JRE.

- Error: `Can not find WiX tools`
 - Instala WiX v3.x o valida que `candle.exe` y `light.exe` existan.
 - Si están instalados, usa el script actualizado (incluye autodeteccion).

- Error de politica de ejecucion de PowerShell
 - Ejecuta con `powershell -ExecutionPolicy Bypass -File ...`.
