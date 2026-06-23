# ══════════════════════════════════════════════════════════════════════════
#  iniciar-microservicios.ps1  —  Ferretería Molina
#
#  PREREQUISITOS:
#   1. MySQL 8 corriendo en localhost:3306
#      - BD "ferreteria" creada (ejecutar DB_FERRETERIA.sql)
#   2. Java 17 instalado en C:\Program Files\Java\jdk-17
#
#  USO: click derecho sobre este archivo > "Ejecutar con PowerShell"
#       o desde terminal: .\iniciar-microservicios.ps1
# ══════════════════════════════════════════════════════════════════════════

# ── Forzar Java 17 ─────────────────────────────────────────────────────
$JAVA17 = "C:\Program Files\Java\jdk-17"
$env:JAVA_HOME = $JAVA17
$env:PATH = "$JAVA17\bin;" + ($env:PATH -replace "C:\\Program Files\\Java\\jdk-2[0-9][^;]*;", "")
$env:PATH = "$JAVA17\bin;" + ($env:PATH -replace "C:\\Program Files\\Common Files\\Oracle\\Java\\javapath;", "")

# ── Ruta base del proyecto ──────────────────────────────────────────────
$BASE = "d:\CIBERTEC\6toCiclo\Modulo02\DESARROLLO DE APLICACIONES WEB II\EVALUACIONES\PROYECTO\ProyectoFerreteria_DAWII"

Write-Host ""
Write-Host "  Java: $(java -version 2>&1 | Select-Object -First 1)" -ForegroundColor DarkGray
Write-Host "  Base: $BASE" -ForegroundColor DarkGray

function Start-MS([string]$label, [string]$dir) {
    Write-Host "  ▶  $label" -ForegroundColor Yellow
    $cmd = "`$env:JAVA_HOME='$JAVA17'; " +
    "`$env:PATH='$JAVA17\bin;' + `$env:PATH; " +
    "cd '$dir'; " +
    "mvn spring-boot:run"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $cmd
}

Write-Host ""
Write-Host "+------------------------------------------------------+" -ForegroundColor Cyan
Write-Host "|   Ferretería Molina — Microservicios v3.0            |" -ForegroundColor Cyan
Write-Host "|   (con Config Server centralizado)                   |" -ForegroundColor Cyan
Write-Host "+------------------------------------------------------+" -ForegroundColor Cyan
Write-Host ""

# ══ ORDEN OBLIGATORIO ═══════════════════════════════════════════════════
# 1. Eureka PRIMERO — registro y descubrimiento de servicios
# 2. Config Server SEGUNDO — centraliza configuración de todos los MS
# 3. Microservicios de negocio (ms-auth, ms-clientes, ms-productos, ms-ventas)
# 4. ms-gateway AL FINAL — necesita que todos estén en Eureka
# ════════════════════════════════════════════════════════════════════════

# 1️⃣  Eureka Server
Start-MS "[1/7] eureka-server   :8761" "$BASE\eureka-server"
Write-Host "      Esperando 25 s para que Eureka esté disponible..." -ForegroundColor DarkGray
Start-Sleep -Seconds 25

# 2️⃣  Config Server
Start-MS "[2/7] config-server   :8888" "$BASE\config-server"
Write-Host "      Esperando 15 s para que Config Server esté disponible..." -ForegroundColor DarkGray
Start-Sleep -Seconds 15

# 3️⃣  ms-auth
Start-MS "[3/7] ms-auth         :8090" "$BASE\ms-auth"
Start-Sleep -Seconds 10

# 4️⃣  ms-clientes
Start-MS "[4/7] ms-clientes     :8093" "$BASE\ms-clientes"
Start-Sleep -Seconds 8

# 5️⃣  ms-productos
Start-MS "[5/7] ms-productos    :8091" "$BASE\ms-productos"
Start-Sleep -Seconds 8

# 6️⃣  ms-ventas
Start-MS "[6/7] ms-ventas       :8092" "$BASE\ms-ventas"
Start-Sleep -Seconds 10

# 7️⃣  API Gateway — siempre último
Start-MS "[7/7] ms-gateway      :8080" "$BASE\ms-gateway"

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║               SISTEMA FERRETERÍA MOLINA INICIADO               ║" -ForegroundColor Green
Write-Host "╠════════════════════════════════════════════════════════════════╣" -ForegroundColor Green
Write-Host "║   Angular Frontend    : http://localhost:4200                  ║" -ForegroundColor White
Write-Host "║   API Gateway         : http://localhost:8080  <- USAR ESTE    ║" -ForegroundColor Cyan
Write-Host "║   Eureka Dashboard    : http://localhost:8761                  ║" -ForegroundColor White
Write-Host "║   Config Server       : http://localhost:8888                  ║" -ForegroundColor White
Write-Host "╠════════════════════════════════════════════════════════════════╣" -ForegroundColor DarkGray
Write-Host "║   Servicios Internos (No acceder directamente)                 ║" -ForegroundColor DarkGray
Write-Host "║                                                                ║" -ForegroundColor DarkGray
Write-Host "║   ms-auth            : 8090                                    ║" -ForegroundColor DarkGray
Write-Host "║   ms-productos       : 8091                                    ║" -ForegroundColor DarkGray
Write-Host "║   ms-ventas          : 8092                                    ║" -ForegroundColor DarkGray
Write-Host "║   ms-clientes        : 8093                                    ║" -ForegroundColor DarkGray
Write-Host "╠════════════════════════════════════════════════════════════════╣" -ForegroundColor Green
Write-Host "║   Credenciales de Acceso                                       ║" -ForegroundColor White
Write-Host "║                                                                ║" -ForegroundColor White
Write-Host "║   ADMIN                                                        ║" -ForegroundColor White
Write-Host "║     Email : admin@ferreteria.com                               ║" -ForegroundColor White
Write-Host "║     Pass  : admin123                                           ║" -ForegroundColor White
Write-Host "║                                                                ║" -ForegroundColor White
Write-Host "║   EMPLEADO                                                     ║" -ForegroundColor White
Write-Host "║     Email : empleado@ferreteria.com                            ║" -ForegroundColor White
Write-Host "║     Pass  : empleado123                                        ║" -ForegroundColor White
Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""
