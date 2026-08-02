@echo off
REM ============================================================
REM Build script for KEmnn (x86/32-bit variant)
REM Requires: JDK 11+ (javac, jar)
REM
REM Notes:
REM - javac.exe from JDK 26, 21, or 11 is available
REM - jar.exe from JDK 26, 21, or 11 is available
REM - SWT JAR: home\swt-win-x86.jar
REM - Uses single-pass compilation to resolve circular deps
REM   between m3g_swerve, micro3d_dll, and KEmulator_base's 3d module.
REM ============================================================

REM If not already under watchdog, re-launch with 60s timeout
if "%1" neq "--watched" (
    if exist "%TEMP%\ke_build_step.txt" del /f /q "%TEMP%\ke_build_step.txt" >nul 2>&1
    powershell -Command "$p = Start-Process -FilePath cmd -ArgumentList '/c \"\"%~f0\"\" --watched' -NoNewWindow -PassThru; $timeout=120; $f=$env:TEMP+'\ke_build_step.txt'; $sw=[System.Diagnostics.Stopwatch]::StartNew(); while (!$p.HasExited -and $sw.Elapsed.TotalSeconds -lt $timeout) { Start-Sleep -Milliseconds 200 }; if (!$p.HasExited) { $step=''; if(Test-Path $f){$step=Get-Content $f -Raw -ErrorAction SilentlyContinue}; Write-Host ('BUILD TIMEOUT after '+[int]$sw.Elapsed.TotalSeconds+'s at step: '+$step.Trim()); $p.Kill(); exit 1 }; exit $p.ExitCode"
    exit /b %errorlevel%
)

setlocal enabledelayedexpansion

set PROJECT_DIR=%~dp0source-code\
set HOME_DIR=%PROJECT_DIR%home
set OUT_DIR=%PROJECT_DIR%build_out
set LIB_DIR=%PROJECT_DIR%lib

set BUILD_START=%time%
set BUILD_STEP=starting
echo %BUILD_STEP% > "%TEMP%\ke_build_step.txt"

REM --- Pick highest available JDK ---
if exist "%JAVA_HOME%\bin\javac.exe" (
    set JAVAC="%JAVA_HOME%\bin\javac.exe"
    set JAR="%JAVA_HOME%\bin\jar.exe"
) else if exist "C:\Program Files\Java\jdk-26\bin\javac.exe" (
    set JAVAC="C:\Program Files\Java\jdk-26\bin\javac.exe"
    set JAR="C:\Program Files\Java\jdk-26\bin\jar.exe"
) else if exist "C:\Program Files\Java\jdk-21.0.10\bin\javac.exe" (
    set JAVAC="C:\Program Files\Java\jdk-21.0.10\bin\javac.exe"
    set JAR="C:\Program Files\Java\jdk-21.0.10\bin\jar.exe"
) else if exist "C:\Program Files\BellSoft\LibericaJDK-21\bin\javac.exe" (
    set JAVAC="C:\Program Files\BellSoft\LibericaJDK-21\bin\javac.exe"
    set JAR="C:\Program Files\BellSoft\LibericaJDK-21\bin\jar.exe"
) else if exist "C:\Program Files\Java\jdk-11\bin\javac.exe" (
    set JAVAC="C:\Program Files\Java\jdk-11\bin\javac.exe"
    set JAR="C:\Program Files\Java\jdk-11\bin\jar.exe"
) else (
    where javac >nul 2>&1
    if !errorlevel! equ 0 (
        set JAVAC=javac
        set JAR=jar
    ) else (
        echo ERROR: No JDK found. Install JDK 11+ and set JAVA_HOME.
        exit /b 1
    )
)

echo Using: %JAVAC%

REM --- Classpath construction ---
set CLASSPATH=

REM Libraries from lib/
for %%J in ("%LIB_DIR%\*.jar") do (
    if defined CLASSPATH (set CLASSPATH=!CLASSPATH!;) else set CLASSPATH=
    set CLASSPATH=!CLASSPATH!%%J
)

REM SWT JAR (Windows x86)
set CLASSPATH=%CLASSPATH%;%HOME_DIR%\swt-win32-x86.jar

REM LWJGL native JARs (needed at compile time for references)
set CLASSPATH=%CLASSPATH%;%HOME_DIR%\lwjgl-natives-windows-x86.jar
set CLASSPATH=%CLASSPATH%;%HOME_DIR%\lwjgl-glfw-natives-windows-x86.jar
set CLASSPATH=%CLASSPATH%;%HOME_DIR%\lwjgl-opengl-natives-windows-x86.jar
set CLASSPATH=%CLASSPATH%;%HOME_DIR%\lwjgl3-swt-windows-x86.jar

REM --- Delete old JARs early to prevent file-lock hangs during compilation ---
if exist "%HOME_DIR%\KEmulator.jar" del /f /q "%HOME_DIR%\KEmulator.jar" >nul 2>&1
if exist "C:\Projects\KEADDON\KEADDON_x32\KEmulator.jar" del /f /q "C:\Projects\KEADDON\KEADDON_x32\KEmulator.jar" >nul 2>&1

REM --- Clean ---
if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"

REM --- Single-pass compile all modules (resolves circular deps) ---
set BUILD_STEP=compiling
echo %BUILD_STEP% > "%TEMP%\ke_build_step.txt"
echo === Compiling all modules in single pass ===

REM Merge all source lists into one
cd /d "%PROJECT_DIR%"
type "%PROJECT_DIR%\src_list_base.txt" > "%OUT_DIR%\all_sources.txt"
echo.>> "%OUT_DIR%\all_sources.txt"
type "%PROJECT_DIR%\src_list_m3g_lwjgl.txt" >> "%OUT_DIR%\all_sources.txt"
echo.>> "%OUT_DIR%\all_sources.txt"
type "%PROJECT_DIR%\src_list_micro3d_gl.txt" >> "%OUT_DIR%\all_sources.txt"
echo.>> "%OUT_DIR%\all_sources.txt"
type "%PROJECT_DIR%\src_list_win.txt" >> "%OUT_DIR%\all_sources.txt"
echo.>> "%OUT_DIR%\all_sources.txt"
type "%PROJECT_DIR%\src_list_updater.txt" >> "%OUT_DIR%\all_sources.txt"
echo.>> "%OUT_DIR%\all_sources.txt"
type "%PROJECT_DIR%\src_list_builder.txt" >> "%OUT_DIR%\all_sources.txt"

REM Javac must run from PROJECT_DIR so relative paths in @file resolve correctly
%JAVAC% -source 8 -target 8 -cp "%CLASSPATH%" -d "%OUT_DIR%" -Xmaxerrs 2000 "@%OUT_DIR%\all_sources.txt" > "%TEMP%\javac_build_x32.log" 2>&1
set JAVAC_EXIT=%errorlevel%
type "%TEMP%\javac_build_x32.log" | findstr /i "error"
del "%TEMP%\javac_build_x32.log" 2>nul
if %JAVAC_EXIT% neq 0 exit /b %JAVAC_EXIT%

REM --- Copy resources ---
set BUILD_STEP=copying resources
echo %BUILD_STEP% > "%TEMP%\ke_build_step.txt"
echo === Copying resources ===
xcopy /e /y "%PROJECT_DIR%src\res\*" "%OUT_DIR%\" >nul

REM --- Create version file ---
echo Git-Revision: n/a > "%HOME_DIR%\version.mf"
if not exist "%OUT_DIR%\META-INF" mkdir "%OUT_DIR%\META-INF"
copy /y "%HOME_DIR%\version.mf" "%OUT_DIR%\META-INF\version.mf" >nul

REM --- Package standalone JAR ---
set BUILD_STEP=packaging JAR
echo %BUILD_STEP% > "%TEMP%\ke_build_step.txt"
echo === Building fat JAR with dependencies ===

set FAT_DIR=%PROJECT_DIR%fatjar_tmp
REM Rename stale dir (rename just changes the directory entry — never hangs, unlike delete on inaccessible files)
if exist "%FAT_DIR%" move "%FAT_DIR%" "%FAT_DIR%_old" >nul 2>&1
mkdir "%FAT_DIR%"

REM Extract needed library JARs only (skip sources, skip platform-native duplicates)
for %%J in ("%LIB_DIR%\*.jar") do (
    set "JNAME=%%~nxJ"
    set "SKIP="
    echo !JNAME! | findstr /i "source" >nul && set SKIP=1
    echo !JNAME! | findstr /i "win32" >nul && set SKIP=1
    if not defined SKIP (
        cd "%FAT_DIR%"
        %JAR% xf "%%J" >nul 2>&1
        cd "%PROJECT_DIR%"
    )
)

REM Extract SWT and LWJGL3-SWT
cd "%FAT_DIR%"
%JAR% xf "%HOME_DIR%\swt-win32-x86.jar" >nul 2>&1
%JAR% xf "%HOME_DIR%\lwjgl3-swt-windows-x86.jar" >nul 2>&1
cd "%PROJECT_DIR%"

REM Remove signature files that cause SecurityException
if exist "%FAT_DIR%\META-INF" (
    del "%FAT_DIR%\META-INF\*.SF" 2>nul
    del "%FAT_DIR%\META-INF\*.RSA" 2>nul
    del "%FAT_DIR%\META-INF\*.DSA" 2>nul
    del "%FAT_DIR%\META-INF\*.EC" 2>nul
    del "%FAT_DIR%\META-INF\*.MF" 2>nul
    rmdir /s /q "%FAT_DIR%\META-INF\maven" 2>nul
    rmdir /s /q "%FAT_DIR%\META-INF\versions" 2>nul
    rmdir /s /q "%FAT_DIR%\META-INF\3rd-party-licenses" 2>nul
)

REM Remove native source files and library folders (source jars leak .c/.h/.java)
del /s /q "%FAT_DIR%\*.java" 2>nul
del /s /q "%FAT_DIR%\*.c" 2>nul
del /s /q "%FAT_DIR%\*.h" 2>nul
del /s /q "%FAT_DIR%\*.cpp" 2>nul
del /s /q "%FAT_DIR%\*.rc" 2>nul
del /s /q "%FAT_DIR%\*.mak" 2>nul
del /s /q "%FAT_DIR%\*.def" 2>nul
del /s /q "%FAT_DIR%\*.xpt" 2>nul
del /s /q "%FAT_DIR%\*.js" 2>nul
del /s /q "%FAT_DIR%\*.html" 2>nul
del /s /q "%FAT_DIR%\chrome.manifest" 2>nul
if exist "%FAT_DIR%\library" rmdir /s /q "%FAT_DIR%\library" 2>nul
if exist "%FAT_DIR%\OSGI-INF" rmdir /s /q "%FAT_DIR%\OSGI-INF" 2>nul
if exist "%FAT_DIR%\about_files" rmdir /s /q "%FAT_DIR%\about_files" 2>nul
if exist "%FAT_DIR%\about.html" del /q "%FAT_DIR%\about.html" 2>nul
if exist "%FAT_DIR%\version.txt" del /q "%FAT_DIR%\version.txt" 2>nul
REM Overlay our compiled classes and resources
xcopy /e /y "%OUT_DIR%\*" "%FAT_DIR%\" >nul

REM Create proper manifest
echo Manifest-Version: 1.0>"%FAT_DIR%\META-INF\MANIFEST.MF"
echo Main-Class: emulator.Emulator>>"%FAT_DIR%\META-INF\MANIFEST.MF"
echo Premain-Class: emulator.Agent>>"%FAT_DIR%\META-INF\MANIFEST.MF"
echo Launcher-Agent-Class: emulator.Agent>>"%FAT_DIR%\META-INF\MANIFEST.MF"
echo.>>"%FAT_DIR%\META-INF\MANIFEST.MF"

REM Copy to clean dir excluding inaccessible macos dir, then archive via jar
set CLEAN_DIR=%PROJECT_DIR%fatjar_clean
if exist "%CLEAN_DIR%" move "%CLEAN_DIR%" "%CLEAN_DIR%_old" >nul 2>&1
robocopy "%FAT_DIR%" "%CLEAN_DIR%" /E /XD "META-INF\macos" /NJH /NJS /NDL /NFL /NP >nul
if %errorlevel% geq 8 echo ERROR: File copy failed & exit /b %errorlevel%
if exist "%HOME_DIR%\KEmulator.jar" del /f /q "%HOME_DIR%\KEmulator.jar" >nul 2>&1
REM jar cfm: create+file+manifest; merge our manifest so Main-Class/Premain-Class are preserved
%JAR% cfm "%HOME_DIR%\KEmulator.jar" "%CLEAN_DIR%\META-INF\MANIFEST.MF" -C "%CLEAN_DIR%" . 2>nul
if %errorlevel% neq 0 echo ERROR: JAR creation failed & exit /b %errorlevel%

REM Verify JAR is valid
%JAR% tf "%HOME_DIR%\KEmulator.jar" >nul 2>&1
if %errorlevel% neq 0 echo ERROR: Generated JAR is corrupt & exit /b %errorlevel%

rmdir /s /q "%CLEAN_DIR%"
if exist "%CLEAN_DIR%_old" start /b "" cmd /c "rd /s /q "%CLEAN_DIR%_old" 2>nul"

REM Clean up fatjar_tmp (freshly created — always works)
rmdir /s /q "%FAT_DIR%"
REM Asynchronously clean old stale dir (won't block)
if exist "%FAT_DIR%_old" start /b "" cmd /c "rd /s /q "%FAT_DIR%_old" 2>nul"

REM Deploy to launch folder
if exist "%HOME_DIR%\KEmulator.jar" (
    copy /y "%HOME_DIR%\KEmulator.jar" "C:\Projects\KEADDON\KEADDON_x32\KEmulator.jar" >nul 2>&1
) else (
    echo WARNING: Output JAR missing - deploy skipped
)

echo.
echo === Done! ===
echo Build time: %BUILD_START% - %time%
set BUILD_STEP=done
echo %BUILD_STEP% > "%TEMP%\ke_build_step.txt"
echo Local:   %HOME_DIR%\KEmulator.jar
echo Launcher: C:\Projects\KEADDON\KEADDON_x32\KEmulator.jar
echo To run: java -javaagent:"%%HOME_DIR%%\KEmulator.jar" -Djava.library.path="%%HOME_DIR%%" -jar "%%HOME_DIR%%\KEmulator.jar"