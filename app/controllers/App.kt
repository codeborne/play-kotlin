package controllers

import models.User
import play.db.jpa.JPA
import play.mvc.Controller
import play.mvc.Scope

class App : Controller() {
  fun index(param: String?) {
    val user = JPA.em().find(User::class.java, 1L)
    renderArgs["param"] = param
    renderArgs["user"] = user
    render()
  }

  operator fun Scope.RenderArgs.set(name: String, value: Any?) {
    put(name, value)
  }
}