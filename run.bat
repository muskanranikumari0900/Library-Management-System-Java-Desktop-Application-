@echo off
echo Compiling Library Management System...

if not exist "bin" mkdir bin

javac -cp "lib/*;src" -d bin src\com\library\model\*.java src\com\library\dao\*.java src\com\library\util\*.java src\com\library\ui\*.java src\com\library\main\*.java

if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b %errorlevel%
)

echo Compilation successful.
echo Starting Library Management System...

java -cp "bin;lib/*" com.library.main.App
pause
