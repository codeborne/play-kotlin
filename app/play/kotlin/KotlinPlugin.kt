package play.kotlin

import controllers.Application
import play.CorePlugin
import play.Play
import play.classloading.ApplicationClasses.ApplicationClass
import play.vfs.VirtualFile

class KotlinPlugin : CorePlugin() {
  init {
    Play.pluginCollection.disablePlugin(CorePlugin::class.java)
  }

  override fun compileSources(): Boolean {
    Play.classes.add(ApplicationClass().apply {
      name = "controllers.Application"
      javaFile = VirtualFile.open("app/controllers/Application.kt")
      refresh()
      compiled(Application::class.java.getResource("Application.class").readBytes())
    })
    return true
  }

  fun ApplicationClass.isKotlin() = javaFile.name.endsWith(".kt")

  override fun enhance(applicationClass: ApplicationClass) {
//    if (!applicationClass.isKotlin())
      super.enhance(applicationClass)
  }
}