$userProfile = [System.Environment]::GetEnvironmentVariable("USERPROFILE")
$jfx = "$userProfile\.m2\repository\org\openjfx"
$mysql = "$userProfile\.m2\repository\com\mysql\mysql-connector-j\9.1.0\mysql-connector-j-9.1.0.jar"
$cp = "target\classes;$mysql"

# Detect Java path
$javaExe = "java.exe"
if (Test-Path "$env:JAVA_HOME\bin\java.exe") {
    $javaExe = "$env:JAVA_HOME\bin\java.exe"
}

# Gather all JavaFX jars from the repo for module path (Targeting 17.0.2)
$jfxJars = Get-ChildItem -Path "$jfx\*\17.0.2" -Recurse -Filter "*-win.jar" -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName } | Select-Object -Unique

if ($jfxJars.Count -lt 4) {
    Write-Host "Error: Could not find JavaFX 17.0.2 jars in $jfx." -ForegroundColor Yellow
    exit 1
}
$modulePath = $jfxJars -join ";"

Write-Host "Starting PetalSuite..." -ForegroundColor Cyan
& $javaExe --module-path "$modulePath" --add-modules "javafx.controls,javafx.fxml" -cp "$cp" com.florist.MainApp
