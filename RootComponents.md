# Root Components #

All other components are contained within a root component. Each root component coordinates message passing and rendering for its child components. In addition to controls specific to each root type, all roots must implement the following 3 common controls. (Roots also have 4 hidden controls - `_add`, `_remove`, `_connect` and `_disconnect` - which are used by the hub to manage children and port connections within each root).

**Common Controls**

  * `start` | - | - | control which takes no arguments and starts the rendering process (the exact details of which will change depending on the type of root eg. for a video root it should start video processing). NB. Control Rate triggers are only sent when rendering.
  * `stop` | - | - | control which takes no arguments and stops the rendering process (opposite of above).
  * `info` | ?(component address) | component info | control which takes an optional component address. If no address is given, this control returns info about the root. If an address is given then this control will return information about that component. An error will be returned if the component does not exist or is not contained in this root.


---


### root:video ###

Container for managing video / image components rendering to screen.

**Ports**

  * _none_

**Controls**

  * `width` | property | number(1..2048) | 640 | Width of output screen. Can only be set when root is not rendering.
  * `height` | property | number(1..2048) | 480 | Height of output screen. Can only be set when root is not rendering.
  * `fps` | property | number(1..100) | 24 | Frames per second. Can only be set when root is not rendering.
  * `full-screen` | property | boolean | false | Render to full-screen (centred) or in a window. Can only be set when root is not rendering.


---


### root:audio ###

Container for managing audio components, input and playback.

This component is in active development. There will be Controls for it!

**Ports**

  * _none_

**Controls**

  * _none_


---


### root:gui ###

Container for managing a graphical user interface (GUI) window.

**Ports**

  * _none_

**Controls**

  * _none_




---
