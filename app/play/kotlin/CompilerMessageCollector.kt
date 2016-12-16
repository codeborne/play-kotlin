package play.kotlin

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.*
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import play.Play

class CompilerMessageCollector() : MessageCollector {
  var hasErrors = false;
  val classesToSources: MutableMap<String, String> = mutableMapOf();

  override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation) {
    val path = location.path
    val position = if (path == null) "" else path + ": (" + (location.line.toString() + ", " + location.column) + ") "

    val text = position + message

    when (severity) {
      OUTPUT -> addClass(message)
      in VERBOSE -> println("VERBOSE" + text)
      in ERRORS -> {
        println("ERRORS" + text)
        hasErrors = true;
      }
      INFO -> println("INFO" + text)
      else -> println("VERBOSE" + text)
    }
  }

  private fun addClass(message: String) {
    val lines = message.split('\n')
    if (lines.size != 4 && lines[0] != "Output:")
      throw RuntimeException("Unsupported compiler output: ${message}")

    classesToSources[lines[1].normalizePath()] = lines[3].normalizePath()
  }

  fun String.normalizePath() = removePrefix(Play.applicationPath.absolutePath).removePrefix("/")

  override fun clear() { }

  override fun hasErrors() = hasErrors
}