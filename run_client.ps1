$userProfile = [System.Environment]::GetEnvironmentVariable("USERPROFILE")
$mysql = "$userProfile\.m2\repository\com\mysql\mysql-connector-j\9.1.0\mysql-connector-j-9.1.0.jar"
$cp = "target\classes;$mysql"

# Detect Java path
$javaExe = "java.exe"
if (Test-Path "$env:JAVA_HOME\bin\java.exe") {
    $javaExe = "$env:JAVA_HOME\bin\java.exe"
}

Write-Host "Starting PetalSuite Remote Client..." -ForegroundColor Cyan
& $javaExe -cp "$cp" com.florist.networking.RemoteClient
