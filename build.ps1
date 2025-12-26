$jfx = "C:\Users\yasse\.m2\repository\org\openjfx"
$mysql = "C:\Users\yasse\.m2\repository\com\mysql\mysql-connector-j\9.1.0\mysql-connector-j-9.1.0.jar"

# Gather all JavaFX jars from the repo for module path
$jfxJars = Get-ChildItem $jfx -Recurse -Filter "*-win.jar" | ForEach-Object { $_.FullName } | Select-Object -Unique
$modulePath = $jfxJars -join ";"

# Compile
Write-Host "Compiling..."
& "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot\bin\javac.exe" `
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
