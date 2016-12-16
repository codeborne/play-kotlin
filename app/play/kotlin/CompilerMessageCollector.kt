package play.kotlin

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.*
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

class CompilerMessageCollector : MessageCollector {
  var hasErrors = false;

  override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation) {
    val path = location.path
    val position = if (path == null) "" else path + ": (" + (location.line.toString() + ", " + location.column) + ") "

    val text = position + message

    when (severity) {
      in VERBOSE -> println("VERBOSE" + text)
      in ERRORS -> {
        println("ERRORS" + text)
        hasErrors = true;
      }
      INFO -> println("INFO" + text)
      else -> println("VERBOSE" + text)
    }
  }

  override fun clear() { }

  override fun hasErrors() = hasErrors
}