package play.kotlin

import org.apache.commons.io.FileUtils
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import play.CorePlugin
import play.Logger
import play.Play
import play.classloading.ApplicationClasses
import play.classloading.ApplicationClasses.ApplicationClass
import play.classloading.enhancers.*
import play.exceptions.UnexpectedException
import play.vfs.VirtualFile
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class KotlinPlugin : CorePlugin() {
  init {
    Play.pluginCollection.disablePlugin(CorePlugin::class.java)
  }

  val k2JVMCompiler = K2JVMCompiler()

  override fun compileSources(): Boolean {
    val arguments = K2JVMCompilerArguments()

    val javaPath = Play.javaPath
    for (virtualFile in javaPath) {
      arguments.freeArgs.add(virtualFile.realFile.path)
    }
    arguments.destination = Play.tmpDir.path + "/kotlin"
    arguments.classpath = System.getProperty("java.class.path")
    val collector = CompilerMessageCollector()
    val exec = k2JVMCompiler.exec(collector, Services.EMPTY, arguments)
    if (exec == ExitCode.OK) {
      println("Success!")
      try {
        collector.classesToSources.forEach { e ->
          Play.classes.add(ApplicationClass().apply {
            name = toClassName(e.key, arguments.destination)
            javaFile = VirtualFile.open(e.value)
            refresh()
            compiled(File(e.key).readBytes())
          })
        }
      } catch (e: IOException) {
        e.printStackTrace()
      }
    } else {
      throw IllegalStateException()
    }
    return false
  }

  override fun detectClassesChange(): Boolean {
    var recompile = false
    for (c in Play.classes.all()) {
      if (c.isKotlin() && c.timestamp < c.javaFile.lastModified()) {
        recompile = true
        break
      }
    }

    if (recompile) {
      compileSources()
      throw RuntimeException("Need reload")
    }

    return false
  }

  private fun toClassName(path: String, top: String) =
      path.removePrefix(top).removePrefix("/").replace('/', '.').removeSuffix(".class")

  fun ApplicationClass.isKotlin() = javaFile.name.endsWith(".kt")

  protected fun enhancers(c: ApplicationClass): Array<Enhancer> {
    if (!c.isKotlin()) defaultEnhancers()
    return arrayOf(
      ContinuationEnhancer(),
      SigEnhancer(),
      ControllersEnhancer(),
      MailerEnhancer(),
      LocalvariablesNamesEnhancer()
    )
  }

  override fun enhance(applicationClass: ApplicationClass) {
    for (enhancer in enhancers(applicationClass)) {
      try {
        enhancer.enhanceThisClass(applicationClass)
      } catch (e: Exception) {
        throw UnexpectedException("While applying " + enhancer + " on " + applicationClass.name, e)
      }
    }
  }
}