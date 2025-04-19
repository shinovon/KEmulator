@rem KEmulator nnx64 starter

@echo off
set dir=%~dp0
set f=%1
if defined f (
start javaw -Duser.dir=%dir% -Djava.library.path=%dir% -Xmx1G -jar "%dir%KEmulator.jar" -jar "%1"
) else (
start javaw -Duser.dir=%dir% -Djava.library.path=%dir% -Xmx1G -jar "%dir%KEmulator.jar"
)