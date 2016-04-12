# Components #

The following pages document the various components available for use in the _praxis_ environment.

  * [Root Components](RootComponents.md)
  * [Core Components](CoreComponents.md)
  * [Video Components](VideoComponents.md)

## Documentation Help ##

The title of each component is the type ID. This is the ID that should be used as the second argument to the `create` command. Type IDs are organised into a hierarchy with colons, eg.

` base-type:sub-type:type-id `

` video:time-fx:difference `

### Ports ###

Ports for each type of component are documented in the following way -

_ID | type direction | (optional argument type) | description_

`input` | control in | number(0..1) | Input that takes a number between 0 and 1

### Controls ###

Controls are documented in one of two ways, depending on whether they are properties or not.

#### Standard Controls ####

Standard controls are documented in the following way.

_ID | (input argument types) | (input argument types) | description_

`count` | string,boolean | number(>=0) | Count the number strings (boolean for ignore case). Return will be greater than or equal to 0.

Empty argument types are specified by a `-`

`trigger` | - | - | Trigger output.


#### Properties ####

Properties are a special type of control (and the most common). To set the value of a property, send it a control call with 1 or more arguments. The control will return those arguments if the property could be changed and the arguments were valid, or an error. To query the value of a property send a control call with zero arguments - the value arguments will be returned.

Properties are documented in the following way.

_ID | property | (argument types) | default | description_

`width` | property | number(1..2048) | 640 | Width of window, between 1 and 2048 pixels. Default is 640.