# Architecture #

This page gives a basic overview of the _Praxis_ architecture, as well as some idea of the script syntax used within projects.  If you're using the _Praxis LIVE_ graphical editors, you don't need to know anything about the scripting syntax, but it might be useful to understand what happens underneath - if you open any .pxr files in a text editor you'll notice that the format they are saved in matches this scripting syntax (which is how the command line player can run them).


## Components ##

_Praxis_ patches are created from a series of components arranged hierarchically. The first hierarchical layer consists of a special type of component called a root. All other components must be contained within a root component. The root component controls all messaging and rendering for its child components. There can be multiple root components in _Praxis_ at any one time (as long as they have unique names), each is in effect its own thread of execution.  Currently there are root components for audio, video, GUI and MIDI (you can run multiple instances of each).

All components have a user defined name (ID), which cannot contain spaces.  Components are referred to (addressed) using a familiar slash-separated syntax, eg.

`/root-id/component-id`

Some components (containers) can themselves contain children, so the hierarchy can be multiple levels deep, eg.

`/root-id/grandparent-id/parent-id/component-id`

Components communicate with each other through two different methods - Ports and Controls.

### Ports ###

Ports are used for sending information between components within the same level of hierarchy (ie. components that share the same parent component). For this reason, root components never have ports. Ports offer a lightweight way of passing information between components, and can carry a variety of information - control arguments (number, string, URLs, etc), video frames, audio buffers, etc. Ports are typed (eg. Control Ports, Video Ports, etc) and only ports of the same type can be connected. Most ports also have a sense of direction (Input or Output) - Input ports cannot be connected to other Input ports, and Output ports cannot be connected with other Output ports.

All ports belong to a particular component and have a unique ID. Ports are addressed using the component address, followed by an exclamation mark, followed by the port ID, eg.

`/root-id/component-id!port-id`

### Controls ###

Controls are used for sending and receiving information from components, especially if the component initiating the communication is in a hierarchy under a different root. Communication is achieved by passing messages called Control Calls. This mechanism is used throughout the _Praxis_ environment to ensure a robust way of communicating between different root processes.

Like ports, controls belong to a particular component and have a unique ID. Addressing for controls is similar to ports, but the exclamation point is replaced with a period (a syntax chosen because of its similarity to method calls - control calls are like an asynchronous method call).

`/root-id/component-id.control-id`

## Script Syntax ##

_Praxis_ scripts are used to set up and control patches. The scripts have a very simple syntax that is loosely inspired by TCL. Scripts consist of a series of commands (either internal functions or control calls) - the _Praxis_ script interpreter runs as a (hidden) root component, and so makes use of control calls for safely creating and communicating with other components.

Each line of the script (as long as it's not empty or a comment) is split into a series of tokens.  The first token is either a control address or an internal function name. Any further tokens are treated as arguments to send to the function or control.  White space is used to split tokens, so tokens with spaces should be enclosed in double quotes. Braces {} can also be used to surround tokens, and these can be nested. Square brackets [.md](.md) are used to insert the results of a command or control call as arguments.

Most functions and controls within _Praxis_ have plain English names - simplicity is always preferred over brevity. However, probably the two most important and frequently used functions within _Praxis_ are symbols.

  * `@` : The At command takes two or three arguments. To create a component you pass in the component address as the first argument and the component type as the second argument. The third argument is an optional script to run within the context of the component address (more on this in a moment). NB. It is also possible to miss out the component type and just provide a script, to allow the running of a script within the context of an existing component.
  * `~` : The Connect command takes two argument, two port addresses, and makes a connection between them.

Both of these commands have an equivalent opposite command. `!@` takes a single argument, a component address, and removes that component. `!~` takes two arguments, two port addresses, and breaks the connection between them.

Hopefully the following example will make things clearer - not that Google Code's syntax highlighting helps!  A graphical version of this script is included in the examples - try comparing the second example to the video.pxr file in that project.

```
# This is a comment line - hash must be first character on line

# First create a root component to manage video
@ /video root:video
  # The following three lines send control calls to set /video properties
  /video.width 400
  /video.height 300
  /video.fps 20
  
# create a component to display a background of white noise
@ /video/noise video:test:noise

# create a component to display a still image (with transparency)
@ /video/image video:still
  # set the image to display
  # the 'file' subcommand ensures that the image is relative to where the script is loaded from
  # quotes ensure the filename is considered a single argument
  /video/image.uri [file "hello world.png"]

# create a video output component
@ /video/window video:output

# connect noise to still image to window output
~ /video/noise!output /video/image!input
~ /video/image!output /video/window!input

# start the video root to show window and start rendering
/video.start

```


This example can however be shortened and simplified by using the contextual option of the `@` command. Controls and child components can then be addressed relatively.  NB. `@` scripts can be nested, allowing the easy control of arbitrarily deep hierarchies.

```
@ /video root:video {
    .width 400
    .height 300
    .fps 20
      
    @ ./noise video:test:noise

    @ ./image video:still {
        .uri [file "hello world.png"]
    }
          
    @ ./window video:output {}
           
    ~ ./noise!out ./image!in
    ~ ./image!out ./window!in
}

/video.start
```