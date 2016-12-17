package controllers;

import models.User;
import play.mvc.Controller;

public class AppJava extends Controller {
  public void index(String param) {
    User user = User.findById(1L);
    renderTemplate("App/index.html", param, user);
  }
}
