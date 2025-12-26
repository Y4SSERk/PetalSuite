$userProfile = [System.Environment]::GetEnvironmentVariable("USERPROFILE")
$jfx = "$userProfile\.m2\repository\org\openjfx"
$mysql = "$userProfile\.m2\repository\com\mysql\mysql-connector-j\9.1.0\mysql-connector-j-9.1.0.jar"

# Detect Java path
$javacExe = "javac.exe"

if (Test-Path "$env:JAVA_HOME\bin\javac.exe") {
    $javacExe = "$env:JAVA_HOME\bin\javac.exe"
}

# Gather all JavaFX jars from the repo for module path (Targeting 17.0.2)
$jfxJars = Get-ChildItem -Path "$jfx\*\17.0.2" -Recurse -Filter "*-win.jar" -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName } | Select-Object -Unique

if ($jfxJars.Count -lt 4) {
    Write-Host "Error: Could not find JavaFX 17.0.2 jars in $jfx." -ForegroundColor Yellow
    Write-Host "Please run 'mvn dependency:resolve' or ensure dependencies are in your local .m2 repo." -ForegroundColor Cyan
    exit 1
}
$modulePath = $jfxJars -join ";"

# Create build directory
if (!(Test-Path "target\classes\fxml")) {
    New-Item -ItemType Directory -Path "target\classes\fxml" -Force | Out-Null
}

# Compile
Write-Host "Compiling..."
& $javacExe `
    --module-path "$modulePath" `
    --add-modules "javafx.controls,javafx.fxml" `
    -cp "target\classes;$mysql" `
    -d "target\classes" `
(Get-ChildItem -Path "src\main\java" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName)

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful."
    
    # Copy Resources
    Write-Host "Copying resources..."
    Copy-Item "src\main\resources\schema.sql" "target\classes\" -Force
    Copy-Item "src\main\resources\fxml\*.fxml" "target\classes\fxml\" -Force
    
    Write-Host "Build complete."
}
else {
    Write-Host "Compilation failed."
}
