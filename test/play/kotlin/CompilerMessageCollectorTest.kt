package play.kotlin

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation.Companion.NO_LOCATION
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.junit.Assert.*
import org.junit.Test
import play.Play
import java.io.File

class CompilerMessageCollectorTest {
  @Test
  fun extractClassesAndSources() {
    Play.applicationPath = File("/path")

    val collector = CompilerMessageCollector()
    collector.report(CompilerMessageSeverity.OUTPUT, "Output:\n" +
        "/path/tmp/kotlin/models/User.class\n" +
        "Sources:\n" +
        "/path/app/models/User.kt", NO_LOCATION);

    assertEquals(mapOf("tmp/kotlin/models/User.class" to "app/models/User.kt"), collector.classesToSources)
  }
}