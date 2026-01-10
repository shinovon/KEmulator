@rem KEmulator nnx64 starter, only for JRE 8!

@echo off
set dir=%~dp0
set f=%1
if defined f (
start javaw -Djava.library.path=%dir% -Xmx512M -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar "%dir%KEmulator.jar" -jar "%1"
) else (
start javaw -Djava.library.path=%dir% -Xmx512M -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar "%dir%KEmulator.jar"
)