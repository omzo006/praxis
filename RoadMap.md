# Development Roadmap #

[![](http://wiki.praxis.googlecode.com/hg/gp.png)](http://plus.google.com/u/0/b/108337709490283211163/)

This is a brief idea of my plans for the development of _praxis_.

  * Praxis core
    * ~~Core API~~
      * ~~Component, container, root container, root hub API.~~
      * ~~Base asynchronous message passing API (controls, calls, arguments)~~
      * ~~Base synchronous message passing API (ports).~~
      * ~~Extension API.~~
    * ~~Base implementations of core API~~
    * Core components
      * Basic (~~property, variable, random argument~~) _short term_
      * File components (~~random file~~) _short term_
      * Math components (~~add, multiply, random number, threshold, scale~~) _short term_
      * Time components (interpolator, governor, ~~timer, delay~~) _short term_
  * Praxis extensions
    * Praxis script extension
      * ~~Basic script syntax.~~
      * Enhanced syntax, variables, basic commands (if, while, etc) _medium term_
      * Multiple script processes _medium term_
      * User defined functions _long term_
    * ~~Asynchronous task extension - coordinate background task such as resource loading~~
    * Embedded compiler extension (possibly using Janino) _medium term_
  * Image / Video components (on top of RIPL library)
    * ~~Video root, server, ports API~~
    * ~~Default Image root, port implementations~~
    * Basic Components
      * ~~Output, splitter, still, snapshot~~
      * Video player and capture API (mostly done) _short term_
      * Video player and capture implementation using GStreamer library (mostly done) _short term_
    * Mix Components
      * ~~Composite component (with alpha and photo composite modes (Add, Sub, Difference, Multiply, Screen, etc))~~
      * ~~Crossfader with multiple composite modes.~~
    * Effect Components
      * ~~Ripple effect~~
      * Basic colour effects (negative, negative value, posterize, etc) _short term_
      * Perspective effects _medium term_
    * Motion components
      * ~~Frame difference component~~
      * Simple motion detection, measurement (mostly done) _short term_
      * Motion tracking _medium term_
    * Enable hardware accelerated surfaces and optimise rendering using existing Java2D pipeline _short term_
    * Hardware accelerate custom surface operations and composites (library possibilities being evaluated) _medium term_
  * Audio components (on top of RAPL library)
    * ~~Audio root, server, ports API~~
    * ~~Default audio root, port implementations~~
    * Basic components
      * ~~Audio Input / Output~~
    * Sampling Components
      * ~~Live looper / recorder~~
    * Mix components
      * ~~Gain~~
      * Crossfader _short term_
      * Mixer _short term_
    * Effects
      * ~~Delay, comb filter,~~ simple overdrive _short term_
      * ~~Filters~~, reverb, chorus (porting from Gervill project) _short term_
      * Granulator _medium term_
    * ~~Jack bindings (using JNA)~~
  * GUI control components (on top of Swing)
    * ~~GUI root, binding API~~
    * ~~Basic GUI API implementation~~
    * Control components
      * Basic - ~~Slider, range slider,~~ combo box, textfield, ~~buttons, file loader~~
      * ~~XY controller~~
      * Audio / Video scrubber with thumbnails _medium term_
    * Control containers
      * ~~Horizontal and vertical panels~~
      * ~~Tabbed panels~~
  * MIDI / OSC control components
    * ~~MIDI root, binding API~~
    * ~~Basic MIDI API implementation~~
    * OSC API _medium to long term_
  * Praxis Player (default distribution)
    * ~~Simple script loading / executing GUI.~~
    * Transition player to Netbeans RCP for easier cross platform deployment and modularization. _medium term_
    * Enhanced live scripting (multiple terminals, shortcuts, etc) _medium term_
    * Graphical connection graph for components (using Netbeans Visual Library) _long term_