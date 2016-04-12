# Video Components #

Video components manipulate video signals and are designed to be used in a root container that can manage video rendering.




---


### video:output ###

If in the first level underneath a root that can render video, this component will provide output to the screen. If further down the hierarchy, it will provide output from its container.

**Ports**

  * `input` | Video In | Video signal to be output.

**Controls**

  * _none_


---


### video:snapshot ###

This component provides the ability to capture and display a still frame from its video input. It extends this ability with the option to fade from the previous captured image to the new one over a period of time, and to mix the new image with the previous one.

**Ports**

  * `input` | Video In | Video signal to capture from.
  * `output` | Video Out | Video output signal.
  * `fade-time` | Control In | number(0..3600) | Period in seconds to fade from previous image to current image.
  * `mix` | Control In | number(0..1) | Amount to mix current image with previous image.
  * `trigger` | Control In | argument | Trigger new snapshot.

**Controls**

  * `fade-time` | property | number(0..3600) | 0 | Period in seconds to fade from previous image to current image.
  * `mix` | property | number(0..1) | 1 | Amount to mix current image with previous image.
  * `trigger` | - | - | Trigger new snapshot.


---


### video:splitter ###

Split the video signal to multiple outputs.

**Ports**

  * `input` | Video In | Video input signal.
  * `output-1` | Video Out | First video output signal.
  * `output-2` | Video Out | Second video output signal.

**Controls**

  * _none_


---


### video:still ###

Output a still image loaded from a file. This component offers an optional input onto which the still image will be drawn; this is only used if the still image doesn't fill the frame, or the image is not opaque. Still images are aligned and resized according to the `align-x`, `align-y` and `resize-mode` properties. Images are loaded in the background, and a signal will be sent from the `ready` port when loaded, or the `error` port if an image could not be loaded from the supplied URI. Control calls to `uri` will not return until the image is loaded (or causes an error). An empty string sent to `uri` will clear the current image - any input signal will show through unchanged.

**Ports**

  * `input` | Video In | Input video signal.
  * `output` | Video Out | Output video signal.
  * `uri` | Control In | URI _or_ string("") | Location of image to load, or empty string to clear.
  * `ready` | Control Out | string("") | Signals new image is loaded.
  * `error` | Control Out | string("") | Signals an error occurred while trying to load image.

**Controls**

  * `resize-mode` | property | string(Crop,Stretch,Scale) | Stretch | Mode to use to resize image if not the same size as output frame.
  * `align-x` | property | number(0..1) | 0.5 | Horizontal alignment used when cropping or scaling image.
  * `align-y` | property | number(0..1) | 0.5 | Vertical alignment used when cropping or scaling image.
  * `uri` | property | URI _or_ string("") | Location of image to load, or empty string to clear.


---


### video:mix:composite ###

Compose the video input source onto the video input destination using the mode and additional opacity as set. This component offers most of the Porter-Duff composite modes, along with some additional common blend modes.

**Composite Modes**

  * SrcOver
  * DstOver
  * SrcIn
  * DstIn
  * SrcOut
  * DstOut
  * SrcAtop
  * DstAtop
  * Xor
  * AddPin
  * SubPin
  * Difference
  * Multiply
  * Screen
  * BitXor

**Ports**

  * `input-src` | Video In | Source input video signal.
  * `input-dst` | Video In | Destination input video signal.
  * `output` | Video Out | Video output signal.
  * `mode` | Control In | string(in Composite Modes) | Composite mode to use.
  * `mix` | Control In | number(0..1) | Opacity of source input.

**Controls**

  * `force-alpha` | property | boolean | false | Whether to force input source to have an alpha channel, even if output doesn't (defaults to same as output).
  * `mode` | property | string(in Composite Modes) | SrcOver | Composite mode to use.
  * `mix` | property | number(0..1) | 1 | Opacity of source input.


---


### video:mix:xfader ###

Cross fade between two video signals, using the mode set.

**Crossfade Modes**

  * Blend
  * AddPin
  * Difference
  * BitXor

**Ports**

  * `input-1` | Video In | First input video signal.
  * `input-2` | Video In | Second input video signal.
  * `output` | Video Out | Video output signal.
  * `mix` | Control In | number(0..1) | Mix amount (0 - all input-1, 1 - all input-2).

**Controls**

  * `mode` | property | string(in Crossfade Modes) | Blend | Crossfade composite mode.
  * `mix` | property | number(0..1) | 0 | Mix amount (0 - all input-1, 1 - all input-2).


---


### video:time-fx:difference ###

Outputs the difference between the current input frame and the previous input frame. Values below the threshold are ignored and will be black or transparent depending on surface opacity. Offers three different modes - Color which outputs the difference of each colour channel, Mono which outputs a greyscale image with the maximum difference of the three colour channels, and Threshold which outputs pure white whereever the difference is greater than the threshold.

**Ports**

  * `input` | Video In | Video input signal.
  * `output` | Video Out | Video output signal.
  * `threshold` | Control In | number(0..1) | Threshold level at which to ignore difference.

**Controls**

  * `mode` | property | string(Color,Mono,Threshold) | Color | Difference mode as described above.
  * `threshold` | property | number(0..1) | 0 | Threshold level at which to ignore difference.


---


### video:time-fx:ripple ###

Simulates water ripples across the input surface. The disturbance input should be a greyscale image which is used to disturb the ripples across the input - black pixels cause no disturbance through to white pixels which cause maximum disturbance. This effect actually uses the blue channel to trigger ripples, so a colour input may have an odd effect.

NB. Additional controls for level and intensity of rippling will be added at a later date.

**Ports**

  * `input` | Video In | Video input signal.
  * `disturbance` | Video In | Video input signal used as a disturbance to trigger ripples.
  * `output` | Video Out | Video output signal.

**Controls**

  * _none_


---
