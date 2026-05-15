@echo OFF
REM Simple test script for the authentication system
echo.
echo ===== Spring Boot 3 Authentication System Tests =====
echo.

REM Test 1: Public endpoint (no auth)
echo Test 1: Public Endpoint (No Auth)
REM Using PowerShell since curl is not available on Windows
powershell -NoProfile -Command "Invoke-WebRequest -Uri 'http://localhost:8080/public/hello' -UseBasicParsing" 2>nul
if errorlevel 1 (
    echo ERROR: Could not connect to server. Make sure the application is running.
) else (
    echo Status: SUCCESS
)
echo.

REM Test 2: Secured endpoint with admin credentials
echo Test 2: Secured Endpoint (Admin Auth)
powershell -NoProfile -Command "$cred = New-Object System.Management.Automation.PSCredential('admin', (ConvertTo-SecureString 'admin123' -AsPlainText -Force)); Invoke-WebRequest -Uri 'http://localhost:8080/api/hello' -Authentication Basic -Credential $cred -UseBasicParsing" 2>nul
if errorlevel 1 (
    echo ERROR: Authentication failed
) else (
    echo Status: SUCCESS
)
echo.

echo ===== Tests Complete =====
echo.
echo Expected Results:
echo - Test 1: Should return "Hello! This is a public endpoint - no authentication required."
echo - Test 2: Should return "Hello admin! This is a secured endpoint."
echo.
echo To test with curl/Postman, use:
echo   curl -u admin:admin123 http://localhost:8080/api/hello

