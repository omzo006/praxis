# Stand-alone Projects #

**_Praxis LIVE_ build:121231 is not yet supported by the harness.**

It is now possible to distribute projects as stand-alone executables using the Praxis stand-alone project harness.  This currently requires a minimal amount of manual setup - a wizard to automate this procedure will be included in a future _Praxis LIVE_ release.

NB. **This is still an experimental feature in testing.** Please file bug reports if you see any issues with it.

## Build process ##

  1. Download and extract the build harness from the [Downloads tab](http://code.google.com/p/praxis/downloads/list).
  1. Rename the main directory to the name of your project (eg. magoria)
  1. Rename the files in the `bin` directory to the name of your project - eg.
    * harness -> magoria
    * harness.exe -> magoria.exe
  1. Rename the files in the `etc` directory to the name of your project - eg.
    * harness.clusters -> magoria.clusters
    * harness.conf -> magoria.conf
  1. Find your `Praxis LIVE` installation directory and copy the `praxis` directory (cluster) into the main directory.
  1. Copy your project(s) into the `harness/projects` directory.  You can use more than one project if you like, as long as they will run together in _Praxis LIVE_ (no clashing root names, etc.)

## Notes ##

You should probably make sure that at least one root component in the project has the `exit-on-stop` property set to `true` (probably a video or GUI one with a window).  Otherwise, the _Praxis_ framework will continue running and your user will have no way to stop your application except by using the task manager.

Settings for things like video renderer, audio library, etc. are taken from the settings in _Praxis LIVE_ on the user's machine (or defaults if not installed).  To override these in your app you can set them in the `harness/config/praxis.properties` file.  eg. to set the app to always use the OpenGL renderer add the line `video.renderer=OpenGL`.  A list of all available settings will be made soon, and the upcoming wizard will make this process easier.

This procedure creates a stand-alone executable, but not an installer.  It is started by using the files in the `bin` directory, and relies on the user having the Java Runtime Environment installed.  It is possible to create an installer and ship your own JRE if you wish (see `etc/*.conf` to set the JRE location).  IzPack might be a good choice for an installer - it's currently being evaluated for inclusion.