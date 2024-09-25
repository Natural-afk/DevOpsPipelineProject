@echo off
REM Navigate to the directory where the SnakeGame.java file is located
cd /d %~dp0

echo Compiling SnakeGame.java...
javac -d ../../../../target/classes SnakeGame.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b %errorlevel%
)

echo Compilation successful!

REM Navigate to the target/classes directory where the compiled .class file exists
cd ../../../../target/classes

echo Running SnakeGame...
java com.mycompany.app.SnakeGame

pause