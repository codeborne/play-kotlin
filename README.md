play-kotlin
===========

This is an example of using Kotlin with Play Framework 1.x.

This is not a reusable module (yet).

The general idea would be to use IDEs incremental compilation when developing the app
and load the class files in DEV mode.

Built-in compiler will then be needed for precompilation/PROD only.

## TODO

- load precompiled classes in DEV mode and reload on class file change (this should work for both Java and Kotlin)
- initialize Kotlin compiler only once (it takes time)
- incremental compilation?
- add a better API for rendering
- add better API for JPA
