# Core Components #

Core components manipulate control signals and are designed to be used in various types of root container.




---


### core:i-rate ###

Sends a signal from its output port whenever the root is started.

**Ports**

  * `output` | control out | string("") | Sends an empty string when root is started.

**Controls**

  * _none_


---


### core:k-rate ###

Sends a signal from its output port on every control frame. The exact definition of _control frame_ is left to each root type, but will generally be every frame for a video root, every audio buffer for an audio root, etc. It is the highest frequency at which changes can be made.

**Ports**

  * `output` | control out | string("") | Sends an empty string on every control frame.

**Controls**

  * _none_


---


### core:property ###

Stores a property, and sends it from its output port whenever a value is received.

**Ports**

  * `output` | control out | argument | Sends value on update.

**Controls**

  * `value` | property | argument | "" | Value of this component.


---


### core:variable ###

Stores a variable and sends it whenever triggered. Unlike _core:property_ it does not send on each update.

**Ports**

  * `value` | control in | argument | Sets value to signal received.
  * `trigger` | control in | argument | Triggers sending of value when a signal is received.
  * `output` | control out | argument | Sends value when triggered.

**Controls**

  * `value` | property | argument | "" | Value of this component.
  * `trigger` | - | - | Trigger sending of value from `output` port.


---


### core:random-arg ###

Stores an array of arguments, and selects and sends one randomly when triggered. If the array is empty, an empty string will be sent instead.

**Ports**

  * `trigger` | control in | argument | Triggers sending of a value when a signal is received.
  * `output` | control out | argument | Sends selected value when triggered.

**Controls**

  * `values` | property | array | {} | Array of possible values.
  * `trigger` | - | - | Trigger sending of value from `output` port.


---


### core:files:random ###

Loads a list of files from a directory. When triggered, a random file is selected from the list, and the URI of this file is sent from the output port. If the file list is empty, an empty string will be sent when triggered. The directory list is loaded in the background so as not to interrupt rendering. When loaded, a signal will be sent from the `ready` port. Loading errors will trigger a signal from the `error` port. Control calls to `directory` will not return until the list is loaded.

**Ports**

  * `directory` | control in | URI _or_ string("") | Directory to scan for files, or empty string to clear current list.
  * `trigger` | control in | argument | Trigger selection and sending of file URI.
  * `output` | control out | URI _or_ string("") | Sends selected file URI when triggered.
  * `ready` | control out | string("") | Sends signal when directory list is loaded.
  * `error` | control out | string("") | Sends signal if directory loading fails.

**Controls**

  * `directory` | property | URI _or_ string("") | "" | Directory to scan for files, or empty string to clear current list.
  * `trigger` | - | - | Trigger selection and sending of file URI from `output` port.


---


### core:math:random ###

Sends a random number between `minimum` and `minimum + range` when triggered. If `range` is zero, the value of `minimum` will be sent when triggered.

**Ports**

  * `minimum` | control in | number | Value of minimum.
  * `range` | control in | number(>=0) | Value of range.
  * `trigger` | control in | argument | Send random number from `output`.
  * `output` | control out | number | Sends random number when triggered.

**Controls**

  * `minimum` | property | number | 0 | Value of minimum.
  * `range` | property | number(>=0) | 1 | Value of range.
  * `trigger` | - | - | Trigger random number to be sent from `output`.


---


### core:math:threshold ###

Takes a number on its `input` port and sends it from its `output-high` port if greater than or equal to the threshold value, or from the `output-low` port if below the threshold value.

**Ports**

  * `threshold` | control in | number | Value of threshold.
  * `input` | control in | number | Input value.
  * `output-low` | control out | number | Sends input value if lower than threshold.
  * `output-high` | control out | number | Sends input value if greater than or equal to threshold.

**Controls**

  * `threshold` | property | number | 0 | Value of threshold.


---


### core:math:scale ###

Takes a number between `x1` and `x2` and scales it between `y1` and `y2`. The input value will be clamped between `x1` and `x2` before scaling. `x1` may be higher than `x2`, and `y1` may be higher than `y2` - this allows inverse scaling.

**Ports**

  * `x1` | control in | number | Value of _x1_.
  * `x2` | control in | number | Value of _x2_.
  * `y1` | control in | number | Value of _y1_.
  * `y2` | control in | number | Value of _y2_.
  * `input` | control in | number | Input value.
  * `output` | control out | number | Outputs scaled value.

**Controls**

  * `x1` | property | number | 0 | Value of _x1_.
  * `x2` | property | number | 1 | Value of _x2_.
  * `y1` | property | number | 0 | Value of _y1_.
  * `y2` | property | number | 1 | Value of _y2_.


---


### core:test:log ###

Outputs any argument received on its `input` port to the system log.

**Ports**

  * `input` | control in | argument | Input argument to log.

**Controls**

  * _none_


---


### core:timing:delay ###

Stores any argument received on its `input` port and sends it after the given delay. Arguments are not queued, so if an argument is waiting to be sent when another one arrives, the original argument is lost and the timer starts again from the current argument. Arguments are always delayed by at least one control frame, even if the time is set to 0.

**Ports**

  * `time` | control in | number(0..3600) | Delay time in seconds (can be fractional).
  * `input` | control in | argument | Argument to delay.
  * `output` | control out | argument | Sends delayed argument.

**Controls**

  * `time` | property | number(0..3600) | 0 | Delay time in seconds (can be fractional).


---


### core:timing:timer ###

Sends a signal (empty string) every `period` seconds while the root is rendering. Signals cannot be sent more often than the control frame frequency, and only one signal will be sent per frame, even if multiple periods have passed.

**Ports**

  * `period` | control in | number(0..3600) | Period time in seconds (can be fractional).
  * `output` | control out | string("") | Sends timer output signal.

**Controls**

  * `period` | property | number(0..3600) | 1 | Period time in seconds (can be fractional).