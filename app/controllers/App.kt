package controllers

import models.User
import play.db.jpa.JPA
import play.kotlin.Controller

class App : Controller() {
  fun index(param: String?) {
    val user = JPA.em().find(User::class.java, 1L)
    render("param" to param, "user" to user)
  }
}