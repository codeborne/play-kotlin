package play.kotlin

import org.apache.commons.io.FileUtils
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.*
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import play.CorePlugin
import play.Play
import play.classloading.ApplicationClasses
import play.classloading.ApplicationClasses.ApplicationClass
import play.vfs.VirtualFile
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class KotlinPlugin : CorePlugin() {
  init {
    Play.pluginCollection.disablePlugin(CorePlugin::class.java)
  }

  class CompilerMessageCollector : MessageCollector {
    var hasErrors = false;

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation) {
      val path = location.path
      val position = if (path == null) "" else path + ": (" + (location.line.toString() + ", " + location.column) + ") "

      val text = position + message

      if (VERBOSE.contains(severity)) {
        println("VERBOSE" + text)
      } else if (ERRORS.contains(severity)) {
        println("ERRORS" + text)
        hasErrors = true;
      } else if (INFO == severity) {
        println("INFO" + text)
      } else {
        println("VERBOSE" + text)
      }
    }

    override fun clear() { }

    override fun hasErrors() = hasErrors
  }

  override fun compileSources(): Boolean {
    val k2JVMCompiler = K2JVMCompiler()
    val arguments = K2JVMCompilerArguments()

    val javaPath = Play.javaPath
    for (virtualFile in javaPath) {
      arguments.freeArgs.add(virtualFile.realFile.absolutePath)
    }
    arguments.destination = Play.tmpDir.absolutePath + "/kotlin"
    arguments.classpath = System.getProperty("java.class.path")
    val exec = k2JVMCompiler.exec(CompilerMessageCollector(), Services.EMPTY, arguments)
    if (exec == ExitCode.OK) {
      println("Success!")
      try {
        val destination = File(arguments.destination)
        val compileFiles = FileUtils.listFiles(destination, arrayOf("class"), true)
        for (classFile in compileFiles) {
          val result = FileInputStream(classFile).readBytes()
          val name = toClassName(classFile, destination)
          if (name.startsWith("play.")) continue

          val ktFile = VirtualFile.fromRelativePath("/app/" + name.replace(".", "/") + ".kt")
          Play.classes.add(ApplicationClasses.ApplicationClass(name, ktFile))
          Play.classes.getApplicationClass(name).compiled(result)
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

  private fun toClassName(file: File, top: File) =
      file.path.removePrefix(top.path).removePrefix("/").replace('/', '.').removeSuffix(".class")

  fun ApplicationClass.isKotlin() = javaFile.name.endsWith(".kt")

  override fun enhance(applicationClass: ApplicationClass) {
    // TODO: check which enhancers are needed by Kotlin classes
    //    if (!applicationClass.isKotlin())
      super.enhance(applicationClass)
  }
}