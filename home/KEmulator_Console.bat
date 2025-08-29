@rem KEmulator nnx64 starter with console window, only for JRE 8!

@echo off
set dir=%~dp0
set f=%1
if defined f (
java -Djava.library.path=%dir% -Xmx512M -jar "%dir%KEmulator.jar" -jar "%1"
) else (
java -Djava.library.path=%dir% -Xmx512M -jar "%dir%KEmulator.jar"
)
pause