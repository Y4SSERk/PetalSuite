@echo off
echo Starting PetalSuite...

set JFX_PATH=C:\Users\yasse\.m2\repository\org\openjfx
set MYSQL_JAR=C:\Users\yasse\.m2\repository\com\mysql\mysql-connector-j\9.1.0\mysql-connector-j-9.1.0.jar

REM Construct module path - simplified for batch (assuming standard layout or listing explicit jars if possible, but for batch dynamic finding is hard. 
REM Since we know the user has the PowerShell command working with the dynamic finding, we'll try to approximate or use the PS script wrapper if possible.
REM However, to be robust in pure Batch, we'd need to list the jars.
REM Let's defer to calling the PowerShell script for simplicity and robustness as user is on Windows.

powershell -ExecutionPolicy Bypass -File "run.ps1"
