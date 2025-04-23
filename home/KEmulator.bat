@rem KEmulator nnx64 starter

@echo off
set dir=%~dp0
set f=%1
if defined f (
start javaw "-Djava.library.path=%dir%" -Xmx512M -jar "%dir%KEmulator.jar" -jar "%1"
) else (
start javaw "-Djava.library.path=%dir%" -Xmx512M -jar "%dir%KEmulator.jar"
)