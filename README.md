# KEmulator nnmod
Cross-platform J2ME emulator, based on decompiled KEmulator 1.0.3

![screenshot](/screenshot.png)

## Links
- [Webpage](https://nnproject.cc/kem)
- [Discord server](https://discord.gg/ETvTpMehXV)
- [Telegram channel](https://t.me/nnmidlets)

## Compatibility
JRE:
- Java 8 by Oracle
- OpenJDK 17-25

System:
- Windows XP - 11 (x86, x86_64, arm64)
- Linux GTK3, glibc 2.17+ (x86, x86_64, arm64, arm32)
- Mac OS X 10.13 and higher (x86_64)
- macOS 11 Big Sur (arm64)
- Termux (arm64)

## Developing in IntelliJ IDEA
### Importing project
- Install Ant plugin
- Open repository root as project in IDEA
### Building
- Build artifacts: `KEmulator_win`, `KEmulator_x64`, `m3g_lwjgl`, `m3g_swerve`, `micro3d_dll`, `micro3d_gl`

## Developing in Eclipse IDE
### Importing project
- Open `eclipse` directory as workspace in Eclipse IDE
- File>Import...>General>Existing Projects into Workspace
- Locate `eclipse` directory, select all projects and click Finish.
### Running
- File>Import...>Run/Debug>Launch Configurations
- Locate `eclipse/run` directory, select all configurations and click Finish
- Fix Runtime JRE if necessary. When running `KEmulator_win`, make sure 32-bit JRE is selected
### Building
- File>Export...>Java>Runnable JAR file
- Select launch configuration, export destination, and click Finish

## Uses
### Libraries
- [SWT](https://www.eclipse.org/swt/) 4.20 / 4.7
- [LWJGL](https://github.com/LWJGL/lwjgl3) 3.3.6
- [JNA](https://github.com/java-native-access/jna) 5.7.0
- [ASM](https://asm.ow2.io/) 5.2
- [vlcj](https://github.com/caprica/vlcj) 4.7.3
- [JInput](https://github.com/jinput/jinput) 2.0.5
- [jutils](https://github.com/jinput/jutils) 1.0.0
- [java-discord-rpc](https://github.com/MinnDevelopment/java-discord-rpc) 2.0.1
- [webcam-capture](https://github.com/sarxos/webcam-capture) 0.3.12
- [BrigJ](https://github.com/nativelibs4java/BridJ) 0.7.0
- [lwjgl3-swt](https://github.com/LWJGLX/lwjgl3-swt)
- org.apache.tools.zip
- [opencore-amr](https://sourceforge.net/projects/opencore-amr) 0.1.6

### Source code
- [J2ME-Loader](https://github.com/nikita36078/J2ME-Loader) Apache-2.0 license
- [JL-Mod](https://github.com/woesss/JL-Mod) Apache-2.0 license
- [FreeJ2ME](https://github.com/hex007/freej2me) GPLv3
- [FreeJ2ME-Plus](https://github.com/TASEmulators/freej2me-plus) GPLv3
- [Symbian^3](https://github.com/SymbianSource) EPL 1.0

### Decompiled code
- KEmulator v0.9.8, v1.0.3 by Wu.Liang
- Nokia S40 SDK
- GLEmulator
