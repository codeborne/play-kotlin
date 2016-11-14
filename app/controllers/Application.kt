package controllers

import models.User
import play.db.jpa.JPA
import play.mvc.Controller

class Application : Controller() {
  fun index(param: String) {
    val user = JPA.em().find(User::class.java, 1L)
    render(param, user)
  }
}