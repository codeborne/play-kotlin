package controllers

import play.mvc.Controller

class Application : Controller() {
  fun index(param: String) {
    render(param)
  }
}