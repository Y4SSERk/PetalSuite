$jfx = "C:\Users\yasse\.m2\repository\org\openjfx"
$mysql = "C:\Users\yasse\.m2\repository\com\mysql\mysql-connector-j\9.1.0\mysql-connector-j-9.1.0.jar"
$cp = "target\classes;$mysql"
$modulePath = (Get-ChildItem $jfx -Recurse -Filter "*-win.jar" | ForEach-Object { $_.FullName } | Select-Object -Unique) -join ";"

Write-Host "Starting PetalSuite Key..." -ForegroundColor Cyan
& "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot\bin\java.exe" --module-path "$modulePath" --add-modules javafx.controls, javafx.fxml -cp "$cp" com.florist.MainApp
