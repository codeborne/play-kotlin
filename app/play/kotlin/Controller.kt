package play.kotlin

import play.mvc.Scope

open class Controller : play.mvc.Controller() {
  operator fun Scope.RenderArgs.set(name: String, value: Any?) = put(name, value)

  fun render(vararg params: Pair<String, Any?>) {
    renderTemplate(template(), params.toMap())
  }

  fun render(templateName: String, vararg params: Pair<String, Any?>) {
    renderTemplate(templateName, params.toMap())
  }
}