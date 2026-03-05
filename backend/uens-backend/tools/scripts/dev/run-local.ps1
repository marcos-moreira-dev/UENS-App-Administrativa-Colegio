#requires -Version 5.1
$ErrorActionPreference = "Stop"

if (-not (Test-Path ".env")) {
  Write-Host "Falta .env (copia .env.example a .env)"
  exit 1
}

# Load .env variables into the current process so Spring can read them.
Get-Content ".env" | ForEach-Object {
  $line = $_.Trim()
  if ($line -and -not $line.StartsWith("#")) {
    $parts = $line -split "=", 2
    if ($parts.Count -eq 2) {
      $name = $parts[0].Trim()
      $value = $parts[1].Trim()
      if (
        ($value.Length -ge 2) -and
        (
          ($value.StartsWith('"') -and $value.EndsWith('"')) -or
          ($value.StartsWith("'") -and $value.EndsWith("'"))
        )
      ) {
        $value = $value.Substring(1, $value.Length - 2)
      }
      [System.Environment]::SetEnvironmentVariable($name, $value, "Process")
    }
  }
}

.\\mvnw.cmd -DskipTests spring-boot:run -Dspring-boot.run.profiles=dev
