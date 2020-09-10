package nl.knaw.huygens.tag.jupyter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import nl.knaw.huygens.graphviz.DotEngine
import nl.knaw.huygens.tag.tagml.*
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path

object TAG {

    fun init() {
        (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.WARN
        println("Welcome to TAG")
        val dotEngine = DotEngine()
        if (dotEngine.hasDot) {
            println("Using GraphViz: ${dotEngine.dotVersion}")
        } else {
            println("No dot executable found. Is GraphViz installed?. GraphViz is needed to render the graphs.")
        }
    }

    fun initCell() {
    }

    fun shutdown() {
        println("Goodbye from TAG")
    }

    fun tokenize(tagml: String): List<TAGMLToken> =
        tokenize(parseFromString(tagml))

    fun tokenize(tagmlPath: Path): List<TAGMLToken> =
        tokenize(parseFromPath(tagmlPath))

    fun tokenize(tagmlFile: File): List<TAGMLToken> =
        tokenize(parseFromFile(tagmlFile))

    private fun tokenize(result: TAGMLParseResult): List<TAGMLToken> {
        if (result.warnings.isNotEmpty()) {
            println("Warnings:\n" + result.warnings.joinToString("\n") { pretty(it) })
        }
        when (result) {
            is TAGMLParseResult.TAGMLParseSuccess -> {
                return result.tokens
            }
            is TAGMLParseResult.TAGMLParseFailure ->
                println("Errors:\n" + result.errors.joinToString("\n") { pretty(it) })
        }
        return listOf()
    }

    private fun pretty(error: ErrorListener.TAGError): String =
        when (error) {
            is ErrorListener.CustomError ->
                "@${error.range.startPosition.line}:${error.range.startPosition.character} .. ${error.range.endPosition.line}:${error.range.endPosition.character}: ${error.message}"

            else -> error.message
        }

}