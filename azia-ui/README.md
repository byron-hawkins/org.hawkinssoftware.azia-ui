Azia User Interface Components
------------------------------

Built on Azia [Core] and [Input], this module serves as the main 
client API for the the [Azia User Interface Library][parent].

[Core]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-core/blob/master/azia-core/README.md
[Input]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-input/blob/master/azia-input/README.md
[parent]: https://github.com/byron-hawkins/org.hawkinssoftware.azia/blob/master/azia/README.md


#### Installation

Add it as a project dependency, like any ordinary Java library.

#### Usage

Refer to the [Azia documentation][website] for tutorials and
reference documentation on the usage of components. Only a few 
basic points are mentioned here.

1. Any entity may receive [UserInterfaceNotification]s about 
   transactional activity by implementing [UserInterfaceHandler]
   and being installed in the relevant [UserInterfaceHandler].Host
    * Generically look up the structurally nearest host by calling 
      <code>[CompositionRegistry].getService(UserInterfaceHandler.Host.class)</code>
    * Request any notification by declaring a public method
      having the notification type (or supertype) as the first
      parameter, and [PendingTransaction] as the second.
       + Notification dispatch will be instrumented at runtime
       + Multiple such methods are allowed
    * State changes in response to notifications must be made by 
      contributing [UserInterfaceDirective]s to the transaction
1. Any entity may similarly receive [UserInterfaceDirective]s 
   during the transaction commit phase:
    * implement [UserInterfaceHandler] 
    * get installed in the relevant [UserInterfaceHandler].Host
    * Declare a public method having a single parameter of the
      relevant directive type 
       + Notification dispatch will be instrumented at runtime
       + Multiple such methods are allowed
    * State changes may be made during execution of these methods
1. All instances of [CompositionElement] will be included in the 
   [CompositionRegistry] for lookup, both as a service and as a
   [composite][AbstractComposite] (if applicable). 
    * The registry will associate each element with the composite
      nearest to it on the call stack at the time of construction.
    * Any element wishing to be notified when its containing 
      composite has been registered may implement 
      [CompositionElement].Initializing
1. Every instance of [AbstractComponent] and [AbstractComposite]
   must be constructed using a [ComponentAssembly] within the 
   execution context of an [InstantiationTask] member class.
    * This process is under substantial revision
1. Implementations of [InstancePainter] are assigned to composites
   by the [PainterRegistry] using the current [PainterFactory]
    * In <code>paint([Canvas])</code> methods, all changes pushed 
      to the canvas will be automatically popped on method exit
       + Manual pop is available for complex painting
       + A call to `Canvas.narrowClip()` will never widen the clip
    * Type variables such as `PainterMarker` refer to marker 
      interfaces which only serve type checking purposes, e.g. to 
      prevent a button painter from being assigned to a scrollbar

[website]: http://www.hawkinssoftware.net/oss/azia
[AbstractComponent]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-ui/blob/master/azia-ui/src/main/java/org/hawkinssoftware/azia/ui/component/AbstractComponent.java
[AbstractComposite]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-ui/blob/master/azia-ui/src/main/java/org/hawkinssoftware/azia/ui/component/composition/AbstractComposite.java
[Canvas]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-ui/blob/master/azia-ui/src/main/java/org/hawkinssoftware/azia/ui/paint/canvas/Canvas.java
[ComponentAssembly]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-ui/blob/master/azia-ui/src/main/java/org/hawkinssoftware/azia/ui/component/ComponentAssembly.java
[CompositionElement]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-ui/blob/master/azia-ui/src/main/java/org/hawkinssoftware/azia/ui/component/composition/CompositionElement.java
[CompositionRegistry]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-ui/blob/master/azia-ui/src/main/java/org/hawkinssoftware/azia/ui/component/composition/CompositionRegistry.java
[InstancePainter]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-ui/blob/master/azia-ui/src/main/java/org/hawkinssoftware/azia/ui/paint/InstancePainter.java
[InstantiationTask]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-core/blob/master/azia-core/src/main/java/org/hawkinssoftware/azia/core/action/InstantiationTask.java
[PainterFactory]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-ui/blob/master/azia-ui/src/main/java/org/hawkinssoftware/azia/ui/paint/PainterFactory.java
[PainterRegistry]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-ui/blob/master/azia-ui/src/main/java/org/hawkinssoftware/azia/ui/paint/PainterRegistry.java
[PendingTransaction]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-core/blob/master/azia-core/src/main/java/org/hawkinssoftware/azia/core/action/UserInterfaceTransaction.java
[UserInterfaceHandler]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-ui/blob/master/azia-ui/src/main/java/org/hawkinssoftware/azia/ui/component/UserInterfaceHandler.java
[UserInterfaceNotification]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-core/blob/master/azia-core/src/main/java/org/hawkinssoftware/azia/core/action/UserInterfaceNotification.java
[UserInterfaceDirective]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-core/blob/master/azia-core/src/main/java/org/hawkinssoftware/azia/core/action/UserInterfaceDirective.java

#### Known Issues and Limitations

1. There are very few components available. As a prototype, the
   goal so far has been to implement a diversity of components,
   rather than a comprehensive library. So there are no menus, 
   combo boxes, tables, etc.
1. Much of the code is experimental, so there is substantial
   disorganization and inconsistency. 