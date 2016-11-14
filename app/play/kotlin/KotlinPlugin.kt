package play.kotlin

import play.CorePlugin
import play.Logger
import play.Play
import play.classloading.ApplicationClasses.ApplicationClass
import play.vfs.VirtualFile
import java.io.File

class KotlinPlugin : CorePlugin() {
  init {
    Play.pluginCollection.disablePlugin(CorePlugin::class.java)
  }

  override fun compileSources(): Boolean {
    val dir = File(javaClass.getResource("/").path)
    Logger.info("Loading precompiled Kotlin classes from $dir")
    addClassesFrom(dir, dir)
    return true
  }

  private fun addClassesFrom(dir: File, top: File) {
    dir.listFiles().forEach { entry ->
      if (entry.isDirectory) addClassesFrom(entry, top)
      else if (entry.name.endsWith(".class")) addClass(entry, top)
    }
  }

  private fun addClass(file: File, top: File) {
    val className = toClassName(file, top)
    if (Play.classes.hasClass(className) || className.contains("Plugin")) return
    Play.classes.add(ApplicationClass().apply {
      name = className
      javaFile = VirtualFile.open(file)
      refresh()
      compiled(file.readBytes())
    })
  }

  private fun toClassName(file: File, top: File) =
      file.path.removePrefix(top.path).removePrefix("/").replace('/', '.').removeSuffix(".class")

  fun ApplicationClass.isKotlin() = javaFile.name.endsWith(".kt")

  override fun enhance(applicationClass: ApplicationClass) {
//    if (!applicationClass.isKotlin())
      super.enhance(applicationClass)
  }
}