package play.kotlin

import play.mvc.Scope

open class Controller : play.mvc.Controller() {
  operator fun Scope.RenderArgs.set(name: String, value: Any?) = put(name, value)
}