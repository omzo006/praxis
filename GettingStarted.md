# _Praxis LIVE_ - Getting Started #

This page gives a brief guide to starting the _Praxis LIVE_ software.  Make sure to also read the _README_ file with the examples - they are documented in there.  More documentation will be added to this wiki in the near future.

## Download Praxis LIVE ##

Download the latest build of _Praxis LIVE_ and the examples from the downloads page.

NB. _Praxis LIVE_ includes the full _Praxis_ framework - you don't need to also download the _Praxis_ command line player.


### Requirements ###

  * _Praxis LIVE_ requires Java 6. If you haven't got Java, try http://www.java.com
  * Playing video files and capturing input from a camera require the GStreamer library.

## Starting Praxis LIVE ##

_Praxis LIVE_ is built on the NetBeans platform and uses its launcher mechanism.

  1. Make sure to unzip both _Praxis LIVE_ and the examples.
  1. Go into the praxis\_live directory, and then the bin directory.
  1. Run either praxis\_live (Linux / Mac) or praxis.exe (Windows).


## Running the example projects ##

The examples directory includes a range of simple examples, as well as two 'real' projects - Magoria, a generative portraits project; and RAM, a live-looping audio sampler.

  1. Using the `File` menu or the popup menu in the Projects tab, select `Open Project` and find one of the example projects.
  1. Once the project is open, use the popup menu on the project or the `Run` menu and select `Run Project`.
  1. Open any of the .pxr files within the project to see the visual editor for that file (the project must be built or running to be able to edit).
  1. Restart the _Praxis_ hub in the Hub Manager tab to clear out the project before running any other examples.
  1. **Read the README file - there's lots more information about each example in there!**

## Quick overview of the visual editor ##

  * Select components to view and edit their properties under the Properties tab.
  * Click on the background of the visual editor to select the root component and change its properties.  NB. Some root properties can't be set or won't take effect without stopping and starting the root - use the popup menu in the Hub Manager.
  * The button next to the property value opens a window with an editor (eg. sliders, file selection, bigger text area etc.)
  * Use the embedded satellite view (bottom right) to pan around the editor.
  * `CTRL+mousewheel` to zoom in and out.
  * Drag and drop components from the palette.
  * Drag the mouse between ports to make connections.
  * Use the popup menu or `delete` key to remove components and connections.
  * Hover over components, ports and connections to find out more information about them (type, etc).

You can save your changes by using the main `File` menu or the popup menu on the editor tab (feedback about saving will be improved in a later release).

NB. As mentioned earlier, you must build or run the project to edit .pxr files in the graph editor.


You can find a more detailed overview of the _Praxis_ architecture on the ArchitectureOverview page.


**NB. If you're using _Praxis LIVE_ on Linux and Gnome, be aware that there is a known bug where the window will disappear from the window list.  You can still `Alt-TAB` to it.  This is a known bug in the interaction between Java and Gnome - I don't think there is anything that could be changed in the _Praxis_ code base to mitigate this.**