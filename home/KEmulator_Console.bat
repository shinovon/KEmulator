@rem KEmulator nnx64 starter with console window

@echo off
set dir=%~dp0
set f=%1
if defined f (
java -Xmx512M -jar "%dir%KEmulator.jar" -jar "%1"
) else (
java -Xmx512M -jar "%dir%KEmulator.jar"
)
pause