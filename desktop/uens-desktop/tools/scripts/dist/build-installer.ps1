# ============================================================
# UENS Desktop - Installer build helper (Windows)
# ============================================================
# Este script encapsula el flujo de distribucion para no depender de
# comandos manuales largos de Maven + jpackage.
#
# Flujo interno:
# 1) valida prerequisitos (jpackage, WiX si aplica)
# 2) compila y empaqueta el modulo desktop
# 3) prepara carpeta de input para jpackage
# 4) genera app-image o MSI
# 5) aplica recursos visuales del MSI si existen (banner/dialog)
#
# Uso rapido:
# powershell -ExecutionPolicy Bypass -File .\tools\scripts\dist\build-installer.ps1 -PackageType msi -AppVersion 1.0.0
param(
    # Tipo de paquete final:
    # - msi: instalador Windows Installer (requiere WiX)
    # - app-image: carpeta portable, sin instalacion formal
    [ValidateSet("msi", "app-image")]
    [string]$PackageType = "msi",

    # Nombre comercial del ejecutable/paquete generado
    [string]$AppName = "UENS-Desktop",

    # Version visible para el usuario y clave para upgrades
    [string]$AppVersion = "1.0.0",

    # Fabricante mostrado por el instalador
    [string]$Vendor = "Marcos Moreira Dev"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

if ($env:OS -ne "Windows_NT") {
    throw "Este script fue disenado para Windows porque MSI depende de herramientas de Windows."
}

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$moduleDir = (Resolve-Path (Join-Path $scriptDir "..\..\..")).Path
$workspaceDir = (Resolve-Path (Join-Path $moduleDir "..\..")).Path
$mavenWrapper = Join-Path $workspaceDir "mvnw.cmd"

if (-not (Test-Path $mavenWrapper)) {
    throw "No se encontro mvnw.cmd en el workspace: $mavenWrapper"
}

if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) {
    throw "No se encontro jpackage en PATH. Usa JDK 21+ y asegurate de tener jpackage disponible."
}

if ($PackageType -eq "msi") {
    # jpackage en Windows delega MSI a WiX v3 (candle + light).
    # Primero probamos PATH actual.
    $hasCandle = Get-Command candle.exe -ErrorAction SilentlyContinue
    $hasLight = Get-Command light.exe -ErrorAction SilentlyContinue

    if (-not $hasCandle -or -not $hasLight) {
        # Fallback: rutas comunmente usadas por instaladores WiX.
        $wixBinCandidates = @(
            "C:\Program Files (x86)\WiX Toolset v3.14\bin",
            "C:\Program Files (x86)\Wix Toolset v3.14\bin",
            "C:\Program Files\WiX Toolset v3.14\bin",
            "C:\Program Files\Wix Toolset v3.14\bin",
            "C:\Program Files (x86)\WiX Toolset v3.11\bin",
            "C:\Program Files\WiX Toolset v3.11\bin"
        )

        foreach ($candidate in $wixBinCandidates) {
            $candlePath = Join-Path $candidate "candle.exe"
            $lightPath = Join-Path $candidate "light.exe"
            if ((Test-Path $candlePath -PathType Leaf) -and (Test-Path $lightPath -PathType Leaf)) {
                # Inyectamos ruta hallada solo para esta ejecucion del proceso.
                $env:PATH = "$candidate;$env:PATH"
                break
            }
        }

        $hasCandle = Get-Command candle.exe -ErrorAction SilentlyContinue
        $hasLight = Get-Command light.exe -ErrorAction SilentlyContinue
    }

    if (-not $hasCandle -or -not $hasLight) {
        throw "Para --PackageType msi debes instalar WiX Toolset (candle.exe/light.exe) y agregarlo al PATH."
    }
}

Push-Location $moduleDir
try {
    # Build completo del modulo + copia de dependencias runtime.
    # target/installer/input sera usado como --input de jpackage.
    & $mavenWrapper -q -DskipTests clean package dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/installer/input

    # Busca el jar principal mas reciente evitando jars auxiliares.
    $mainJar = Get-ChildItem -Path "target" -Filter "uens-desktop-*.jar" |
        Where-Object { $_.Name -notlike "*sources*" -and $_.Name -notlike "*javadoc*" } |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1

    if (-not $mainJar) {
        throw "No se encontro el JAR principal en target/."
    }

    $inputDir = "target/installer/input"
    if (-not (Test-Path $inputDir)) {
        New-Item -ItemType Directory -Path $inputDir -Force | Out-Null
    }
    Copy-Item -Path $mainJar.FullName -Destination (Join-Path $inputDir $mainJar.Name) -Force

    # Icono principal de launcher/installer.
    $iconPath = "src/main/resources/assets/pictures/icons/logoIcon.ico"
    $baseArgs = @(
        "--type", $PackageType,
        "--name", $AppName,
        "--input", $inputDir,
        "--main-jar", $mainJar.Name,
        "--main-class", "com.marcosmoreiradev.uensdesktop.app.AppLauncher",
        "--icon", $iconPath,
        "--dest", "target/installer",
        "--app-version", $AppVersion,
        "--vendor", $Vendor
    )

    if ($PackageType -eq "msi") {
        # Personalizacion opcional del asistente MSI:
        # - banner.bmp (franja superior)
        # - dialog.bmp (panel lateral)
        $installerImagesDir = "src/main/resources/assets/pictures/installer images"
        $bannerBmp = Join-Path $installerImagesDir "banner.bmp"
        $dialogBmp = Join-Path $installerImagesDir "dialog.bmp"

        if ((Test-Path $bannerBmp -PathType Leaf) -and (Test-Path $dialogBmp -PathType Leaf)) {
            $bannerAbs = (Resolve-Path $bannerBmp).Path
            $dialogAbs = (Resolve-Path $dialogBmp).Path
            $bannerXml = [System.Security.SecurityElement]::Escape($bannerAbs)
            $dialogXml = [System.Security.SecurityElement]::Escape($dialogAbs)

            $resourceDir = "target/installer/jpackage-resource-dir"
            New-Item -ItemType Directory -Path $resourceDir -Force | Out-Null

            # Reemplazamos ui.wxf por una version con WixUIBannerBmp/WixUIDialogBmp.
            $uiTemplate = @"
<?xml version="1.0" ?>
<Wix xmlns="http://schemas.microsoft.com/wix/2006/wi" xmlns:util="http://schemas.microsoft.com/wix/UtilExtension">
  <Fragment>
    <WixVariable Id="WixUIBannerBmp" Value="$bannerXml" />
    <WixVariable Id="WixUIDialogBmp" Value="$dialogXml" />
    <Property Id="WIXUI_INSTALLDIR" Value="INSTALLDIR"></Property>
    <UI Id="JpUI">
      <UIRef Id="WixUI_InstallDir"></UIRef>
      <DialogRef Id="InstallDirNotEmptyDlg"></DialogRef>
      <Publish Dialog="WelcomeDlg" Control="Next" Event="NewDialog" Value="InstallDirDlg" Order="6">NOT Installed</Publish>
      <Publish Dialog="InstallDirDlg" Control="Back" Event="NewDialog" Value="WelcomeDlg" Order="6">NOT Installed</Publish>
    </UI>
  </Fragment>
</Wix>
"@
            Set-Content -Path (Join-Path $resourceDir "ui.wxf") -Value $uiTemplate -Encoding UTF8
            $baseArgs += @("--resource-dir", $resourceDir)
        }

        # Flags de UX instalador en Windows.
        $baseArgs += @("--win-dir-chooser", "--win-menu", "--win-shortcut")
    }

    # Ejecuta empaquetado final.
    & jpackage @baseArgs
    if ($LASTEXITCODE -ne 0) {
        throw "jpackage termino con codigo $LASTEXITCODE"
    }

    Write-Host ""
    Write-Host "Instalador generado correctamente en:"
    Write-Host "  $moduleDir\target\installer"
    Write-Host ""
}
finally {
    Pop-Location
}
