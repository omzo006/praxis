# Release Notes for Praxis LIVE #

## Praxis LIVE build:130508 ##

This release brings a wide range of changes, some developed for a paid commission in the first few months of the year that have taken a little while to tidy up for release.  All should now be back in sync and we'll be back to more incremental releases.  Highlights (some in test) include container components, OSC control, transformed (eg. rotated) video, further OpenGL performance improvements, copy & paste, subgraph imports (macros / abstractions), and (finally!) a stereo sample player.

### New features ###

  * **Core**
    * **User Containers**. Support for creating container components (`core:container`) that can contain other components, with properties (`core:container:property`) and ports (`audio:container:*`, etc.) showing on the parent. Currently marked as test status - use `Tools/Options` to enable showing of test components.
    * **Code Param animation modes**. Animatable parameters in custom code components now support a variety of interpolators, based on CSS options - linear (default), ease, easeIn, easeOut, easeInOut. Can be used by doing eg. `p(1).to(0.5).in(5).easeInOut();`
    * **Component MetaData**. Component factories can now provide additional information about components. Biggest visible change for now is that test components will now have the ID they're intended to have on release. Test components show in italic in the palette. Also support for marking component deprecation and replacements - will make it easier to provide new components into the future.
    * **TextEditor GLSL support**. The text editor now has basic GLSL syntax support.
  * **Audio**
    * **Stereo sample player** at `audio:sampling:player`. Marked as test, though due to some plans to refactor this further for the next release - should be stable. Use `Tools/Options` to get it to show in the palette. Requested by Amélie Polachowska.
    * **JAudioLibs - separate repository**. Much of the audio code in Praxis can be used by any Java application. Though it has been released separately for some time, development has now been moved to encourage reuse - https://github.com/jaudiolibs/
  * **Video**
    * **Transformed blitting**. Support for drawing transformed, particularly rotated, video. Only accessible through custom code components at the moment - new `draw()` functions `rotate()`, `translate()`, `scale()`, `resetMatrix()`. Should work identically to their Processing counterparts (2D only).
    * **Renderer setting per root**. It's now possible to override the renderer setting on a per-root basis. Primary usage is to allow for a low-resolution, low-framerate software pipeline to do motion detection / tracking to control a high-resolution OpenGL pipeline.
    * **(OpenGL) pipeline improvements**. Texture caching and modification tracking ensure non-GL data is only uploaded when necessary. Native pixel data implementation allows direct transfer of GStreamer video data to OpenGL without entering Java memory. SurfaceOps now reusable (mutable). Combined these provide a massive performance boost for working with video sources and still images.
  * **OSC**
    * **New support for OSC input**. Start of OSC support in Praxis through the NetUtil library (http://www.sciss.de/netutil/). Currently only input, UDP and running as a server is supported. OSC input components require a control address - as these are OSC compatible, the control address is the default OSC identifier, but can be overridden. Default port for the server is `1234` but can be changed. The `root:osc` component will not show in the template menu by default - use `New / `Other...` Requested by Ryan Webber.
  * **TinkerForge**
    * **Update to API v2**. The TinkerForge (http://www.tinkerforge.com/) bindings have been updated to version 2 of the API, which work a lot better in the context of how Praxis uses them. Still only support for the starter kit components - more to come.
  * **Praxis LIVE**
    * **Copy & paste**. It's now possible to copy & paste components (or multiple components and connections). A dialog provides intelligent (but overridable) renaming where necessary. It is achieved by saving a section of the graph as text to the clipboard (of which more in a moment). Currently only enabled in the graph editor.
    * **SubGraph (.pxg) files and import**. SubGraph files are designed to fulfil the need for code reuse, macros, custom components, etc. Use the `File Browser` to find the file and drag into the editor (graph editor only for now). Uses the same infrastructure as copy & paste. Longer term, support will be provided for importing these to the palette. There is no export at the moment - however, you can easily create a SubGraph file by selecting the relevant components and copy & pasting them into a text editor, making sure to save the file with a `.pxg` extension.
    * **Graph editor container support**. The graph editor now supports user containers. Drag a `core:container` component onto the graph. You can then select `Open` in the component's popup menu to add components to it. Use the up arrow in the toolbar to return to the parent level. Note that `core:container:property` controls and container ports show up on the parent.
    * **Other graph editor UI improvements**. Rounded corners. Move all selected components together. Ports disappear when zooming out (CTRL-mousewheel) to speed up rendering and allow easier selection / dragging. Did I mention rounded corners! :-)
    * **OSC editor**. Editor implementation for OSC bindings based on MIDI editor.

### Issues fixed ###
  * New template management based around component metadata should stop an exception that was being reported with the TinkerForge root.
  * Switched default file encoding to UTF-8 on all platforms. Should fix problems with Russian (and other!) characters in filenames. [Issue 29](https://code.google.com/p/praxis/issues/detail?id=29) reported by Paddy Duncan.

### Known issues ###
  * **Paste & Import can take a long time in an active root.** See [issue 33](https://code.google.com/p/praxis/issues/detail?id=33).
  * **Copy & paste not syncing containers correctly**. The copy action does not currently sync children of containers (as save does). Workaround - open the container and select all components to sync before copying.
  * **Exceeding maximum audio channels hangs audio root.** Trying to set the number of audio channels above what your system can support may hang the audio root and require the hub to be restarted.
  * **Windows**
    * **Incorrect shortcut icon.** In a few cases the shortcut icon for Praxis LIVE is incorrect. Right click on the shortcut, choose `Properties` - `Shortcut` - `Change Icon` and select the correct (only available) icon.

### Before installing / using this build ###

If you have previously used an earlier build of Praxis LIVE, you are recommended to uninstall the older version before installing this one, including deleting the user directory.  **If you have installed the GStreamer plugin you will have to manually delete this** (this problem is now fixed so won't be necessary in future!).

If you have been using the ZIP distribution, you should consider deleting your user configuration directory before running this build.

This should be found at -

  * Windows - C:\Documents and Settings\YOURUSERNAME\.praxis\_live
  * Windows (Vista or 7) - C:\Users\YOURUSERNAME\AppData\Roaming\.praxis\_live
  * Mac OSX - /Users/YOURUSERNAME/.praxis\_live
  * Linux - /home/YOURUSERNAME/.praxis\_live



---


## Praxis LIVE build:121231 ##

This build is re-based on NetBeans platform 7.2 and brings a range of improved editing functionality.  The video pipeline has also seen some major work (along the lines done previously on the audio pipeline), with performance and stability improvements.

### New features ###

  * **Core**
    * **Dynamic Components**. Support for components to change configuration (controls / ports) at runtime.  Initially supports the multi-channel audio improvements (below), this will also allow for supporting sub-component graphs amongst a range of other future features.
    * **New Text Editor API**. Improved syntax highlighting text editor components (based on RSyntaxTextArea). This is currently only used within _Praxis LIVE_, but a component for control panels will follow.
  * **Audio**
    * **Multi-channel audio support**.  It is now possible to use up to 16 channels for input and output (system dependent).  Use the `.channels` control on the `audio:input` or `audio:output`.  NB. This value is only read when the audio first starts, so additional channels will be silent until you restart audio.  Also note the issue below.
  * **Video**
    * **Re-factored and improved video pipeline**. The video pipeline has had some major work to improve performance and stability, particularly of the OpenGL renderer (which is now marked "beta" rather than "Highly Experimental"!).  This work also paves the way for further improvements and features in the next release.
    * **GStreamer plugin for Windows included**.  The GStreamer plugin for Windows is now included in the installer for Windows and the full version of the Zip distribution, allowing for video playback and capture without extra installation.  Also useful for distributing stand-alone projects.
  * **TinkerForge**
    * A start has been made on bindings for TinkerForge components (http://www.tinkerforge.com/) for various forms of physical computing.  Currently the components from the starter kit (LCD20x4, Rotary pot, IR distance sensor, Light sensor) are supported.  To create a TinkerForge root, go to `New` / `Other...` and select `Praxis Root File`.  You can then select the `root:tinkerforge` type.  It's a little hidden away as development is waiting on v2.0 of the TinkerForge protocol to be released.
  * **Praxis LIVE**
    * **Upgraded to NetBeans Platform 7.2**. Bringing improved performance and bug fixes, as well as a few nice new features, including -
      * **Document tab groups** Have multiple editors open and visible at the same time, great for larger screens.  Simply right-click on the tab of an open editor and select the option to open in a new group.
      * **Project import / export** Import and Export projects directly to from Zip files (in the `File` menu).
    * **Improved Resource and Control Address dialogs.**  The editors for resources (files) and controls addresses (bindings, send, etc.) have been improved.  The Resource editor now includes a file browser of the Resources folder of the project - external files can still be selected using the browse button.  The Control Address editor now allows you to browse through a tree of all components and controls to visually select the one you want.
    * **Improved Code Editor**.  The code editor dialog has been improved using the new Text Editor API mentioned above.  A new toolbar allows you to send (compile) the code without closing the window, or re-sync to the existing code.
    * **Import Resources**.  A new item in the popup menu for the Resources folder and sub-folders allows you to select files to import into your project.
    * **Graph editor improvements**.  The graph editor now saves the minimized state of components, allowing for more compact graphs to be saved. There have also been a number of tweaks to the appearance of the graph.
    * **Linux .deb installer**.  The default installer for Linux is now a .deb installer which installs _Praxis LIVE_ and the _Praxis_ command line player for system-wide usage.  Non-installed usage is still possible using the Zip distribution, but the .deb is the preferred option.

### Issues fixed ###
  * Unity / Gnome issues seem to mostly be fixed by switching to the .deb installer.
  * The new Text Editor API (and removal of JSyntaxPane) removes the Null Pointer Exception issues when closing text files.

### Known issues ###

  * **Exceeding maximum audio channels hangs audio root.** Trying to set the number of audio channels above what your system can support may hang the audio root and require the hub to be restarted.
  * **Windows**
    * **Incorrect shortcut icon.** In a few cases the shortcut icon for Praxis LIVE is incorrect. Right click on the shortcut, choose `Properties` - `Shortcut` - `Change Icon` and select the correct (only available) icon.


### Before installing / using this build ###

If you have previously used an earlier build of Praxis LIVE, you are recommended to uninstall the older version before installing this one, including deleting the user directory.  **If you have installed the GStreamer plugin you will have to manually delete this** (this problem is now fixed so won't be necessary in future!).

If you have been using the ZIP distribution, you should consider deleting your user configuration directory before running this build.

This should be found at -

  * Windows - C:\Documents and Settings\YOURUSERNAME\.praxis\_live
  * Windows (Vista or 7) - C:\Users\YOURUSERNAME\AppData\Roaming\.praxis\_live
  * Mac OSX - /Users/YOURUSERNAME/.praxis\_live
  * Linux - /home/YOURUSERNAME/.praxis\_live



---


## Praxis LIVE build:120912 ##

This is a minor bug fix release with a small number of new but useful features.  It paves the way for a range of improvements coming over the next couple of months, including the imminent release of a mechanism for distributing projects as standalone executables.

### New features ###

  * **Core**
    * **Command line player**. This release sees the return of the _Praxis_ command line player, now included as part of the _Praxis LIVE_ distribution. Simply call the player with a project directory, project file (.pxp) or script.  eg. `./praxis "examples/01 Hello World"` or `praxis.exe "examples/01 Hello World"`
    * **Exit Handlers**. It is now possible to exit if a root stops (eg. window is closed). The core now has exit handlers, and each root component now has an `exit-on-stop` property. If this property is true when a root stops then an exit handler is called. In _Praxis LIVE_ this will cause all other roots to stop; when running from the command line or standalone (coming soon) this will cause the application to exit.
  * **Praxis LIVE**
    * **Improved Argument property editors**. It is now possible to use the file (resource) and file list editors when editing properties of the generic `Argument` type. Most usefully, this allows you to set `core:property` or `core:variable` values to relative files without needing an extra script.

### Issues fixed ###
  * A problem with `setup()` never being called in the `core:code:custom` component, and general issues with when `setup()` was called with `video:code:composite` components, has been fixed. **NB. `setup()` is now called every time the root is started rather than just when the code is first installed on the component**.
  * A rare issue with audio that could lead to an invalid JavaSound mixer being chosen has been fixed.
  * Audio input is now not opened if there is no `audio:input` component in the graph.  While this fixes certain issues, it also means that **an `audio:input` component will not work if added to a running audio graph** - you will need to restart the audio root.
  * JACK audio now works on Windows 64 with a 64bit JACK and JVM.

### Known issues ###
  * Clicking on run Praxis LIVE at the end of installation doesn't work on all Linux systems.  Just run from the menu.
  * Praxis disappearing from launcher on Ubuntu Unity.  This is a weird bug in Unity and affects other applications too. Don't use the desktop icon.  After install, log out and back in.  Search for Praxis in the dash, then when running lock the icon to the launcher.  If all else fails, you can find the window using `super W`.
  * Various windows disappearing from window list on Linux GNOME. This is a bug between Java and GNOME, and should be fixed in recent GNOME versions. There is nothing Praxis LIVE can do to alleviate this, but you can always find the windows using `Alt-TAB`.
  * Null pointer exception when closing text files (ie. the readme files - you're reading them, right! :-) ).  Doesn't cause any problems except for the notification.  Will be fixed in a future version.


### Before installing / using this build ###

If you have previously used an earlier build of Praxis LIVE, you are recommended to uninstall the older version before installing this one, including deleting the user directory.  **If you have installed the GStreamer plugin you will have to manually delete this** (fix coming!).

If you have been using the ZIP distribution, you should consider deleting your user configuration directory before running this build.

This should be found at -

  * Windows - C:\Documents and Settings\YOURUSERNAME\.praxis\_live
  * Windows (Vista or 7) - C:\Users\YOURUSERNAME\AppData\Roaming\.praxis\_live
  * Mac OSX - /Users/YOURUSERNAME/.praxis\_live
  * Linux - /home/YOURUSERNAME/.praxis\_live



---



## Praxis LIVE build:120620 ##

This release brings a range of new components, and some important improvements.  The audio pipeline has seen some major work, and the API has been rationalised and made public.  The video pipeline now supports capture devices across all OS (OSX not tested), and live coding of GLSL shaders.  A GStreamer plugin is also available for Windows to ease use of video on that platform, and various other improvements have been made to Windows support.


### New features ###

  * **Core**
    * **`core:code:custom`** - new component for live-coding control signals.
    * **`core:routing:gate`** - new component for gating control signals. Supports simple on/off usages, as well as a simple pattern sequencer. See example `07 audio seq` for usage.
    * **`core:routing:inhibitor`** - new component to limit messages passing through within a given time.
    * **`core:routing:send`** - new component for sending control signals, in particular to components in different roots.
    * **Template support**. Properties can now provide a template when the value is empty.  Useful for code components to provide a default structure.
  * **Audio**
    * **`audio:analysis:level`** - new component for measuring the level (using RMS) of the audio.
    * **`audio:modulation:chorus`** - new chorus / flanger effect.
    * **`audio:modulation:lfo-delay`** - new delay effect, using the same underlying code as the chorus, but with more extreme possibilities.
    * **`audio:reverb:freeverb`** - a port of the common freeverb effect to Praxis.
    * **Link ports**.  Various audio components now support link ports, which allow components to sync parameters - useful for applying the same effect across multiple channels (most Praxis components are mono rather than making assumptions on how many channels you want to work with).
    * **API**.  The audio API has been rationalised and made public, simplifying development and meaning it is now possible to create audio plugins for Praxis (example to come soon).
    * **Internal buffer size / timing**.  Audio now runs with a fixed internal buffersize of 64 samples (unless overridden in the audio root configuration), providing better timing.  The JavaSound audioserver now uses a delay-locked loop to correct for jitter between the system clock and the soundcard clock, which should also improve timing accuracy.
  * **Video**
    * **`video:analysis:difference`** - new component for computing the difference of two signals, with various modes and threshold support.
    * **`video:analysis:frame-delay`** - does what it says!  Useful for passing the previous frame into one channel of `video:analysis:difference`, etc.
    * **`video:analysis:simple-tracker`** - new component that does simple blob tracking by tracking the largest blob in an image. See example `08 blobs` for usage.
    * **`video:opengl:filter`** - new component for live coding GLSL fragment shaders.  Only works in the OpenGL pipeline (obviously!), which can be set in `Tools / Options / Video`.  See example `09 GLSL` for usage.
    * **Cross-platform capture support**.  The `video:capture` component should now work cross-platform.  A generic capture:// uri is used in the `device` property, which translates to the correct driver for the platform.
    * **GStreamer Windows plugin**.  A plugin is available on the Downloads page to provide a local version of GStreamer for video capture and playback.
  * **GUI**
    * **`gui:combobox`** - new component to provide a combobox, which automatically syncs to available values for bindings that support it.  See the revised example `03 audio gui` for usage.  **_Component requested by Chuck Ritola - more requests welcomed!_**
    * **`gui:textfield`** - a simple bindable textfield.
  * **Documentation**
    * **Component help files**.  All components now have help files.  Access through `Help / Help Contents`, search using the quick search box (top right), or select a component in the palette and press `F1`.
    * **Manual / live-coding documentation**.  A short manual as well as a wiki page documenting the live-coding API are in the works and first drafts should be up in the next few weeks.

### Issues fixed ###
  * A rare hard lockup when dropping components from the palette should now be fixed.
  * Problems when an older version of the JNA library was on the system should now be fixed.
  * OpenGL full screen on Windows should now work correctly.
  * The executable on Windows should now have the correct icon.

### Known Issues ###

  * Praxis disappearing from launcher on Ubuntu Unity.  This is a weird bug in Unity and affects other applications too. Don't use the desktop icon.  After install, log out and back in.  Search for Praxis in the dash, then when running lock the icon to the launcher.  If all else fails, you can find the window using `super W`.
  * Various windows disappearing from window list on Linux GNOME. This is a bug between Java and GNOME, and should be fixed in recent GNOME versions. There is nothing Praxis LIVE can do to alleviate this, but you can always find the windows using `Alt-TAB`.
  * Null pointer exception when closing text files (ie. the readme files - you're reading them, right! :-) ).  Doesn't cause any problems except for the notification.  Will be fixed in a future version.


### Before using this build ###

If you have previously used an earlier build of Praxis LIVE, you are recommended to uninstall the older version before installing this one.

If you have been using the ZIP distribution, you should consider deleting your user configuration directory before running this build.

This should be found at -

  * Windows - C:\Documents and Settings\YOURUSERNAME\.praxis\_live
  * Windows (Vista or 7) - C:\Users\YOURUSERNAME\AppData\Roaming\.praxis\_live
  * Mac OSX - /Users/YOURUSERNAME/.praxis\_live
  * Linux - /home/YOURUSERNAME/.praxis\_live



---



## Praxis LIVE build:120430 ##

This is mainly (at least on the surface!) a bug fix release, with a few minor interface improvements.  This release also features some improvements to the OpenGL pipeline, a move towards finalizing the plugin API, and a range of new components in testing (enable test components in the Options window, though be aware that test components are still subject to change).

### New features ###

  * **Slider-style property editors**.  Improved support for editing numeric values (only those that have a defined range so far).  Just drag within the property area to adjust values.  A single click will still allow the value to be entered manually (more [here](http://praxisintermedia.wordpress.com/2012/04/16/slider-style-property-editors/)).
  * **Root configuration button**.  It's now possible to open a property dialog for the root component from a new button on the editor toolbar.  This makes it much easier to get to properties such as video dimension, video framerate, audio samplerate, etc.

### Issues fixed ###
  * The `audio:sampling:looper` component was not initializing properly, causing the end point of the loop to be 0.0 instead of 1.0.
  * The `core:timing:animator` component was not correctly managing having its `value` and `to` properties set in the same frame.
  * Full screen support is fixed in the OpenGL pipeline.  However, you may still encounter some difficulties on Windows - please ask on the [mailing list](http://groups.google.com/group/praxis-intermedia) about a possible workaround.
  * Some minor bugs in the examples have been fixed, primarily in the RAM loop sampler, which was affected by the `audio:sampling:looper` bug above.

### Known Issues ###

  * Various windows disappearing from window list on Linux GNOME. This is a bug between Java and GNOME, and should be fixed in recent GNOME versions. There is nothing Praxis LIVE can do to alleviate this, but you can always find the windows using `Alt-TAB`.


### Before using this build ###

If you have previously used an earlier build of Praxis LIVE, you are recommended to uninstall the older version before installing this one.

If you have been using the ZIP distribution, you should consider deleting your user configuration directory before running this build.

This should be found at -

  * Windows - C:\Documents and Settings\YOURUSERNAME\.praxis\_live
  * Windows (Vista or 7) - C:\Users\YOURUSERNAME\.praxis\_live
  * Mac OSX - /Users/YOURUSERNAME/.praxis\_live
  * Linux - /home/YOURUSERNAME/.praxis\_live



---


## Praxis LIVE build:120123 ##

After a couple of months of hectic development, this is the first full release of _Praxis LIVE_.  This release brings development of the core framework (_Praxis_) and the visual editor (_Praxis LIVE_) in sync.  All features of the _Praxis_ core can now be edited from within _Praxis LIVE_.

There have been some revisions to the core architecture, which means that projects from earlier releases may not be fully compatible.  The examples have all been updated, and it is planned to maintain backwards compatibility from now on.

Does this mean that development is finished?  No, far from it!  The basic core is now completed and usable.  However, _Praxis / Praxis LIVE_ will continue to evolve with new components and features, and incremental releases every 4-6 weeks.

### New Features ###

  * **Visual GUI editor.** Edit control panels 'live'.  Opening the .pxr file for GUI's will switch the control panel from a separate window to the editor area.  Switch into `edit` mode using the toolbar button (or `CTRL-e`) to activate the edit overlay.  Drag components from the palette on to the panel, or move existing components using the arrow keys.  Drag and drop of existing components, as well as many more improvements, coming soon.
  * **MIDI editor.** Edit MIDI input bindings within a simple table overview.  Connect MIDI input to any control.
  * **Component editor dialogs.**  Rather than using the `Properties` window, you can now double-click any component within the graph, GUI or MIDI editors to open an editor window, with access to all properties and actions.  Dialogs are non-modal, so you can open multiple editors at a time, or continue to work within the main window while the editor windows are open.
  * **Rewritten Save infrastructure.** The save infrastructure has been rewritten to ensure that all properties are correctly synced prior to saving.
  * **Better hub management and data-loss prevention.** The infrastructure for managing the hub has been enhanced.  In particular, any operation that might result in data being lost through root deletion, hub restart or application exit, will trigger a dialog allowing saving, discarding or cancelling.
  * **Project warnings / errors UI.** Errors and warnings triggered during the building of projects are now reported to the user.
  * **Transient properties.** Components now have the ability to report transient properties, which should not be saved.  There are some properties for which saving is not usually the right operation.  Editors do not save transient properties, and transient properties are marked in italic within the `Properties` window and editor dialogs.
  * **Consistent interfaces.** Many of the components have been refactored to ensure a consistent naming and ordering of controls and ports throughout the framework.
  * **Graph editor performance improvements.** Graph editors now perform much better when lots of components and connections are on screen.
  * **General UI overhaul**.  In particular, there is now a more consistent icon style and colour scheme across the application.
  * **Installers.**  There are now installers for Windows and Linux, providing ease of use and a smaller download size.  There are still some minor issues being ironed out, and the zip distribution will continue to be made available, in particular for Mac users.  (If you'd be happy to try out a Mac installer, please [contact me](http://neilcsmith.net/contact)).
  * **Many minor tweaks, bug fixes and performance improvements.**

### Issues fixed ###

  * Deletion of project files should now be handled correctly.  Renaming of .pxr files is now not allowed (instead of breaking).  These files should always be named after the root ID of the root they define.
  * Data loss issues are fixed by improvements mentioned above.

### Known Issues ###

  * The OpenGL renderer doesn't currently support full screen mode.
  * Various windows disappearing from window list on Linux GNOME. This is a bug between Java and GNOME, and should be fixed in recent GNOME versions. There is nothing Praxis LIVE can do to alleviate this, but you can always find the windows using `Alt-TAB`.


### Before using this build ###

If you have previously used an earlier build of Praxis LIVE, you are recommended to delete your user configuration directory before running this build.

This should be found at -

  * Windows - C:\Documents and Settings\YOURUSERNAME\.praxis\_live
  * Windows (Vista or 7) - C:\Users\YOURUSERNAME\.praxis\_live
  * Mac OSX - /Users/YOURUSERNAME/.praxis\_live
  * Linux - /home/YOURUSERNAME/.praxis\_live



---


## Praxis LIVE EA:111130 ##

### New Features ###

  * New OpenGL video rendering infrastructure based on LWJGL, with some additional code from libGDX.  This is still under active development and only the most common blitting operations are currently accelerated - other operations are done in software.  This renderer is not enabled by default (go to `Tools/Options/Video`)
  * Return of the Praxis command line player, which now understands Praxis LIVE project directories as well as individual scripts.
  * New Settings API with control panel (`Tools/Options`) for default settings.  Settings are stored in the Java preferences system, so will be picked up by other Praxis based applications (currently just the command line player).  Some of the examples have been revised not to override default settings.
  * Projects system improvements.
    * Separate templates and icons for different types of `.pxr` file (Audio Patch, Video Patch, etc.)
    * `Build` and `Run` actions are always enabled and the system tries to be intelligent about what files need to be executed.
    * The system will attempt to ignore errors, allowing projects to be fixed.
    * The `Clear All` action has been removed from the Projects popup menu - use the `Restart Hub` button in the Hub Manager.
  * Various UI improvements, including -
    * Popup menu on components in the graph editor now includes trigger actions (to start video capture, play audio samples, etc.)
    * Toolbar support for editors.
    * Start and stop roots in the Hub Manager by double clicking.
  * Improvements to the component info system (how Praxis components report their capabilities to Praxis LIVE).  Much of the benefit of this will be noticeable in the next release.
  * JNAJack and the AudioServers API have had a number of fixes, in particular to work properly on Windows.


### Bug Fixes ###

  * Dynamic addition and removal of audio inputs / outputs and video outputs should now be possible.


### Known Issues ###

  * The OpenGL renderer doesn't currently support full screen mode.
  * Renaming and deleting of files within a project is not properly supported.  You will also have to manually change or remove the files from the project configuration - `Properties` in the project's popup menu.
  * There is currently no visual indication of modified root files or warning of data loss when restarting the hub. Root files (`.pxr`) can be saved at any time from the `File` menu or the editor tab menu. Fix in progress.
  * Various windows disappearing from window list on Linux GNOME. This is a bug between Java and GNOME, and should be fixed in recent GNOME versions. There is nothing Praxis LIVE can do to alleviate this, but you can always find the windows using `Alt-TAB`.


### Before using this build ###

If you have previously used an earlier build of Praxis LIVE, you are recommended to delete your user configuration directory before running this build.

This should be found at -

  * Windows - C:\Documents and Settings\YOURUSERNAME\.praxis\_live
  * Windows (Vista or 7) - C:\Users\YOURUSERNAME\.praxis\_live
  * Mac OSX - /Users/YOURUSERNAME/.praxis\_live
  * Linux - /home/YOURUSERNAME/.praxis\_live


---


## Praxis LIVE EA:110730 ##

### New Features ###

  * Upgrade to NetBeans 7 platform, and upgrade of all other 3rd-party dependencies.
  * New syntax highlighting for script files and Java fragments, based on the lightweight JSyntaxPane library (this has also allowed the removal of various NetBeans platform modules, resulting in a much smaller download size).
  * Improved performance of Jack audio bindings.
  * Various UI improvements.
  * JNAJack and the AudioServers API have been tidied up for release as standalone libraries.

### Bug Fixes ###

  * Due to an inability to make connections in the patcher on Mac, connections no longer require use of the CTRL key - just drag the mouse between ports.


### Known Issues ###

  * Dynamic addition and removal of audio inputs / outputs and video outputs is not currently supported. When creating a new audio or video Root file, you must include an output component (and input component if required) before starting the root in the hub. Inputs and outputs should also not be deleted. Fix in progress.
  * There is currently no visual indication of modified root files or warning of data loss when restarting the hub. Root files (.pxr) can be saved at any time from the File menu or the editor tab menu. Fix in progress.
  * Various windows disappearing from window list on Linux GNOME. This is a bug between Java and GNOME, and should be fixed in recent GNOME versions. There is nothing Praxis LIVE can do to alleviate this, but you can always find the windows using Alt-TAB.


### Before using this build ###

If you have previously used an earlier build of Praxis LIVE, you are recommended to delete your user configuration directory before running this build.

This should be found at -

  * Windows - C:\Documents and Settings\YOURUSERNAME\.praxis\_live
  * Windows (Vista or 7) - C:\Users\YOURUSERNAME\.praxis\_live
  * Mac OSX - /Users/YOURUSERNAME/.praxis\_live
  * Linux - /home/YOURUSERNAME/.praxis\_live