# KEADDON

KEmulator fork

<img width="1920" height="1080" alt="were3232423423" src="https://github.com/user-attachments/assets/6b102bef-8763-4fdf-9477-82f6c784545e" />

This is a feature fork of [KEmulator nnmod](https://nnproject.cc/kem) (originally by shinovon). It preserves full backward compatibility while adding significant new functionality, a modernized launcher, and deeper control over the emulation experience.

---

## Architectural Changes & New Features

### 1. Favorites Browser (`FavoritesBrowser.java`)

A full-featured JAR browser that replaces the plain file-open dialog as the primary way to launch MIDlets.

- **Three view modes**: *Detailed* (table with name/vendor/version/date/size), *Compact* (inline row with configurable info mask), and *Icons* (visual grid with MIDlet icons)
- **Search bar** with real-time filtering
- **Keyboard/numpad navigation** — arrow keys, numpad 2/4/5/6/8, F2/F3, Enter, numpad `*`/`/` for focus switching
- **Scroll acceleration** — holding a direction key speeds up after 500ms
- **Animated selection rows** — hue cycling based on icon color palette (or average hue), with per-jar palette extraction
- **Persistent settings** — UI font size, text font size, padding, view mode, mask, dark/light theme per mode (`Settings.favoritesUISize_*`, `favoritesTextSize_*`, `favoritesPadding_*`, `favoritesMask_*`, `favoritesIconSize_icons`, `favoritesNames_icons`)
- **Swap directory** — toggle between the favorites folder and the currently loaded JAR's parent folder
- **Move current JAR to favorites** — schedules a file copy executed on next launch (avoids locking)
- **Inline loading toast** with background icon extraction via `ZipFile`/`Manifest`
- **Fullscreen mode** overlay support
- **JAR cache** — remembers last selected folder and index; Prev/Next JAR buttons with auto-repeat long-press

### 2. Lucky Folder System (`LuckyFolderManager.java`, `LuckyFolderSetupDlg.java`)

Allows the user to define multiple directories ("Lucky Folders") that the emulator scans for JAR files.

- Each folder can be in one of two modes:
  - **Favorites Browser mode** — jars appear as a browsable tab in the Favorites Browser
  - **Random mode** — one random JAR is loaded when "Open Lucky Jar" is triggered
- `Settings.luckyFolderPaths` / `Settings.luckyFolderFavBrowserMode` persist the configuration
- `Settings.luckyFolderBrowserIndex` tracks which folder (or the main Favorites folder) is currently active in the browser

### 3. Hotkey Manager (`HotkeyManager.java`)

A centralized, user-configurable hotkey system replacing hard-coded accelerators.

- All actions are defined as `HotkeyAction` instances with `id`, `displayName`, `description`, `defaultKeyCode`, and `defaultStateMask`
- Loaded from / saved to `property.txt` as `Hotkey_<id>=<stateMask>_<keyCode>`
- Conflict detection via `checkConflict()`
- UI navigation actions (`UI_UP`, `UI_DOWN`, `UI_LEFT`, `UI_RIGHT`, `UI_CONFIRM`, `UI_CANCEL`, `UI_SWITCH_LEFT`, `UI_SWITCH_RIGHT`) mapped to numpad keys
- Supported actions include: cycle resolution, open favorites, add to favorites, delete JAR, prev/next JAR, fullscreen, capture, speed controls, suspend/resume, pause/step, x-ray, rotation, force paint, zoom, cycle scaling mode, cycle interpolation, toggle theme, cycle BG animation, open lucky jar, setup lucky folder, and more

### 4. Dark Mode / Theme System

Applied across the entire UI (junky as hell, but that's the windows problem, not mine):

- `Settings.favoritesDarkMode` toggles between light and dark themes
- `EmulatorScreen.applyThemeToShell()` handles SWT color overrides
- `setShellDarkMode()` calls Windows `DwmSetWindowAttribute` (attributes 19/20) for native dark title bar
- `notifyThemeChanged()` propagates theme to all open `themeable` shells
- Theme-aware colors cached statically: `getThemeDarkBg()`, `getThemeDarkFg()`, `getThemeDarkBtnBg()`, `getThemeDarkTextBg()`, `getThemeLightBg()`, `getThemeLightFg()`
- Theme support added to: `EmulatorScreen`, `About`, `AppSettingsUI`, `FavoritesBrowser`, `InfosBox`, `InputDialog`, `KeyPad`, `Log`, `M3GViewUI`, `MediaView`, `MemoryView`, `MessageConsole`, `Methods`, `Property`, `ScreenSizeDialog`, `Watcher`, `IdeaSetup`

### 5. Splash Screen / Background Animation ("Performance Scene")

**I'VE SPEND TOO MUCH TIME TRYING TO MAKE IT WORK AS IT SHOULD.**

The splash screen (shown when no MIDlet is loaded) now features:

- **Pulsing background hue** — a sine-wave color cycle applied to the entire background
- **Infinite-scrolling icon grid** — reads all JARs in the configured directory, extracts their MIDlet icons, and scrolls them in a configurable direction (8 cardinal directions + random). Scroll speed 24px/s at 20 FPS, with fade-in/out at edges
- **Floating inspirational sentences** ("Fun is infinite!", "For free?!", etc.)
- **Two animated buttons** — Browse (purple), Lucky Jar (blue) with color cycling that pauses while the Favorites Browser is open. A third Debug button was present in the original code but has been fully commented out (`/* Debug button removed */` across 22 locations in `EmulatorScreen.java`)
- **Browse button plays sound** — clicking "Browse" plays a random audio file from the `just_to_feel_something` subfolder of the user's data directory. Playback tries three backends in order: JavaSound (`javax.sound.sampled.Clip`), JavaFX (`javafx.scene.media.MediaPlayer`), and JLayer (`jl1.0.1.jar` for MP3)
- **Dark-mode-aware colors** for splash text and background
- `Settings.bgAnimMode` controls the scroll direction (0–7 = directions, 8 = random)

### 6. J2ME Gamepad Integration

Automatic detection, launch, and termination of `J2MEGamepad.exe`:

- `Emulator.isJ2MEGamepadAvailable()` — checks via `tasklist` and caches the executable path via `wmic`
- `Emulator.launchJ2MEGamepad()` — spawns the gamepad process
- `Emulator.killJ2MEGamepad()` — terminates on emulator exit
- `Settings.j2meGamepadEnabled` / `Settings.j2meGamepadAutoLaunch` persist user preference
- Menu items under Midlet menu to toggle termination and auto-launch

### 7. Exit to Favorites & Restart Clean

- `Emulator.exitToFavorites()` — restarts the emulator process with `-fav` flag, showing the Favorites Browser directly
- `Emulator.restartClean()` — strips JAR/JAD arguments from the command line and restarts without loading any MIDlet
- MIDlet's `notifyDestroyed()` now calls `exitToFavorites()` instead of `Runtime.halt(0)`, returning to the browser

### 8. JAR Management

- **Delete current JAR** (`Ctrl+W`) — permanently deletes the JAR file, removes it from MRU list, then loads the next alphabetical JAR from the same folder (or restarts clean)
- **Previous/Next JAR in folder** (`Ctrl+[` / `Ctrl+]`) — sorted alphabetically, with a toast notification showing the filename, delays 800ms before loading
- **Add to Favorites** (`Ctrl+B`) — schedules a file copy to the favorites folder via `Settings.pendingFavoriteMoves`, executed on next launch; automatically navigates to the next JAR
- `Settings.lastJarDir` / `Settings.lastJarIndex` persist the last-visited JAR directory for the cache

### 9. Resolution Preset Cycling

- `cycleResolutionPreset(int direction)` — cycles through `Settings.resolutionPresets` (49 entries from 96×64 to 800×480)
- `Settings.resolutionRestartMidlet` — if enabled, changes resolution by restarting the MIDlet; otherwise resizes the display buffer on-the-fly
- Toast notification showing the new resolution name
- Menu items for Previous/Next preset (`Alt+Left` / `Alt+Right`)

### 10. Toast Notification System

`EmulatorScreen.showToast(text, durationMs)` creates a temporary `SWT.NO_TRIM` shell overlaying the main window with semi-transparent background, auto-disposed after the given duration.

### 11. Window Resize Modes

Extended from the original `Fit`/`Manual` to four modes:

- **Center screen** (`Manual`) — fixed zoom, window stays centered
- **Fit window** (`Fit`) — zoom to fill available space
- **Fit window (Integer scaling)** (`FitInteger`) — same but rounds zoom down to the nearest integer factor (or 1/n for downscaling)
- **Sync screen size** (`FollowWindowSize`) — MIDlet resolution follows window size

### 12. Screen Rotation

- 90° incremental rotation (`Ctrl+Shift+Y`) via `EmulatorScreen.rotate90degrees()` alongside the existing swap (`Ctrl+Y`)
- Full transform support in `updateCanvasRect()` including correct coordinate mapping for paint, pointer, and infos

### 13. Property File Persistence (New Settings)

New settings persisted to `property.txt`:

| Key                                     | Description                                                         |
| --------------------------------------- | ------------------------------------------------------------------- |
| `FavoritesPath`                         | Path to the favorites directory                                     |
| `FavoritesViewMode`                     | "detailed" / "compact" / "icons"                                    |
| `FavoritesUISize_*`                     | Per-mode UI font size                                               |
| `FavoritesTextSize_*`                   | Per-mode text font size                                             |
| `FavoritesPadding_*`                    | Per-mode row/grid padding                                           |
| `FavoritesMask_*`                       | Per-mode compact info mask (bit flags: vendor, version, date, size) |
| `FavoritesNames_icons`                  | Show/hide names below icons in Icons mode                           |
| `FavoritesIconSize_icons`               | Icon pixel size in Icons mode                                       |
| `FavoritesDarkMode`                     | Dark/light theme toggle                                             |
| `PendingFavoriteMoves`                  | Serialized pending file copy operations                             |
| `LastJarDir` / `LastJarIndex`           | JAR cache state                                                     |
| `LuckyFolderBrowserIndex`               | Active browser tab index                                            |
| `LuckyFolderPaths` / `LuckyFolderModes` | Lucky folder configuration                                          |
| `Hotkey_*`                              | Per-action hotkey overrides (state mask + key code)                 |
| `DisableTouchDoubleClick`               | Touch input tweak                                                   |
| `ResolutionRestartMidlet`               | Restart behavior on resolution change                               |
| `FPSCounter`                            | FPS display toggle                                                  |
| `StoreCreatedImages`                    | Memory view setting (already present in original, retained)         |

### 14. Branding

- Version string now includes " KEADDON" suffix: `Emulator.getTitle()` returns `"KEmulator 2.21.4 KEADDON - <MIDlet-Name>"`
- About dialog title changed to "About KEmnnx64"
- New splash image resource `/res/keaddon.png`
- New unknown app icon resource `/res/unknown.png`

### 15. Enhanced IntelliJ IDEA Support (`IdeaSetup.java`, `IdeaUtils.java`)

**idk why it writes about it like that, that's 100% side effect from changes - probably from adding dark-theme.**

- Project creation, restoration, conversion, and editing via CLI: `-new-project`, `-restore`, `-convert`, `-edit`
- Automated download of J2ME Docs and ProGuard
- JDK table patching for IDEA project templates
- Dark mode theme support in the setup dialog

### 16. Modified Core Files

| File                                          | Changes                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| --------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `Emulator.java`                               | Added `exitToFavorites()`, `restartClean()`, J2MEGamepad lifecycle, `lastJarDir` saving, `showFavoritesOnStart`/`startFullscreen` flags, `-fav` and `-fullscreen` CLI args, `customTitle` property, `KEADDON` version string, `syncPresetIndex()` for resolution tracking; `debugBuild` changed to `false`; `notifyDestroyed()` no longer calls `CustomMethod.close()`; `getAboutString()` uses platform-specific title; restart-on-error changed from `System.exit(1)` to `restartClean()`; app settings dialog gated by `Settings.showAppSettingsOnStart`; `loadGame()` conditionally kills J2MEGamepad |
| `Settings.java`                               | ~50 new fields for favorites UI, lucky folders, hotkeys, dark mode, JAR management, resolution cycling, etc.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `EmulatorScreen.java`                         | Vastly expanded: splash screen animation, animated buttons, toast system, resolution cycling, JAR management, theme system, `openFavoritesBrowser()`, `addCurrentToFavorites()`, `doDeleteJar()`, `browseJarInFolder()`, `cycleResolutionPreset()`, hotkey filter, keyboard mode, glow selection animation, `showToast()`, `markThemeable()`/`applyThemeToShell()`/`applyStatusBarTheme()`                                                                                                                                                                                                                |
| `Property.java`                               | Load/save new favorites, lucky folder, and hotkey properties; added hotkey tab to settings dialog; theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `Controllers.java`                            | No significant changes (identical to original)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `CustomMethod.java`                           | `close()` now calls `Emulator.killJ2MEGamepad()`; `kemulator.touch.enabled`, `kemulator.set.title=`, `kemulator.threadtrace` property handling already present in original                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `KeyMapping.java`                             | MIDDLE device keycode (`deviceKeycodes[16]`) changed from Enter (13) to F3 (12); `replaceKey()` removed the `canvasKeyboard` digit early-return (`if (Settings.canvasKeyboard && n >= '0' && n <= '9') return String.valueOf(n)`)                                                                                                                                                                                                                                                                                                                                                                         |
| `MIDlet.java`                                 | `notifyDestroyed()` calls `Emulator.exitToFavorites()` instead of `CustomMethod.close(); System.exit(0)`; VLC protocol handling already present in original                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `Canvas.java`                                 | `getGameAction()` added `AppSettings.upKey <= 0` (etc.) guards before numpad fallbacks, allowing custom key mappings to override defaults                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `AppSettingsUI.java`                          | Added Tweaks tab (key repeats, pointer events, fullscreen ignore, J2L FPS limiter, Motorola soft-key fix, key-on-repeat, sync events, async flush, startApp on resume), 3D tab (M3G LWJGL and MascotCapsule LWJGL settings), final "Save as default" button; theme support                                                                                                                                                                                                                                                                                                                                |
| `About.java`                                  | Redesigned with TabFolder (Changelog, Supported APIs, Credits tabs), GitHub link, dark theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `KeyPad.java`                                 | All buttons changed from `SWT.PUSH` to `SWT.PUSH                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `InfosBox.java`                               | Added theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `Log.java`                                    | Layout margins changed from 1 to 0; `setTopIndex()` call wrapped in dark-mode conditional; added theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `MessageConsole.java`                         | GridLayout refactored with explicit `marginWidth=0`/`marginHeight=0`; added theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `M3GViewUI.java`                              | Added theme support (`markThemeable`/`applyThemeToShell`); render-invisible toggle, axis/grid overlay, keyboard/mouse camera controls already present in original                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `M3GViewCameraSetDialog.java`                 | Added theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `MediaView.java`                              | Added theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `MemoryView.java`                             | `horizontalSeparator` background color made theme-dependent; background/foreground colors conditionally assigned per dark/light mode with proper disposal; added theme support                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `Methods.java`                                | Added theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `Watcher.java`                                | Added theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `ScreenSizeDialog.java`                       | Added theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `InputDialog.java`                            | Added theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `SWTFrontend.java`                            | Removed `Settings.showLogFrame` save in `disposeSubWindows()` — log frame visibility no longer tracked across restarts                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `IdeaSetup.java`                              | Added dark theme support, ProGuard auto-download, local docs path selection                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `IdeaUtils.java`                              | Added theme support                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `win\src\main\emulator\EmulatorPlatform.java` | Platform name "KEmnn 2.21.4 KEADDON"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `x64\src\main\emulator\EmulatorPlatform.java` | Platform name "KEmnnx64 2.21.4 KEADDON"; Termux/Android support via `Utils.termux` — sets LWJGL library path to `/data/data/com.termux/files/usr/lib`, adjusts M3G context mode, returns `"android"` as OS name                                                                                                                                                                                                                                                                                                                                                                                           |
| `emulator\Utils.java`                         | Added `public static final boolean termux` field that detects Termux via `java.vm.vendor` and `user.dir`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `emulator\debug\Memory.java`                  | `isNotInitialized()` refactored to use reflection (`shouldBeInitialized` method) for Java 22+ compatibility; `StoreCreatedImages` support already present in original                                                                                                                                                                                                                                                                                                                                                                                                                                     |

### 17. New Dependencies

- `lib/jl1.0.1.jar` — JavaLayer (MP3 playback) library included in the build

### 18. Build System

- New build scripts: `build_x32.bat`, `build_x64.bat` (with `.bak` originals preserved)
- Deploy script: `deploy_jre_x64.bat`
- Updated `src_list_*.txt` files for new source file inclusion
