package nl.knaw.huygens.tag.jupyter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import nl.knaw.huygens.TAGErrorUtil
import nl.knaw.huygens.graphviz.DotEngine
import nl.knaw.huygens.pretty
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
        tokenize(tagml, parse(tagml))

    fun tokenize(tagmlPath: Path): List<TAGMLToken> =
        tokenize(tagmlPath.toFile().readText(), parse(tagmlPath))

    fun tokenize(tagmlFile: File): List<TAGMLToken> =
        tokenize(tagmlFile.readText(), parse(tagmlFile))

    private fun tokenize(tagml: String, result: TAGMLParseResult): List<TAGMLToken> {
        val u = TAGErrorUtil(tagml)
        if (result.warnings.isNotEmpty()) {
            println("Warnings:\n\n" + result.warnings.joinToString("\n\n") { "* " + u.errorInContext(it).pretty() })
        }
        when (result) {
            is TAGMLParseResult.TAGMLParseSuccess -> {
                return result.tokens
            }
            is TAGMLParseResult.TAGMLParseFailure -> {
                throw(TAGMLParseError("\nErrors:\n\n" + result.errors.joinToString("\n\n") {
                    "* " + u.errorInContext(it).pretty()
                }))
            }
        }
    }

    fun inferHeader(tagmlPath: Path): String =
        inferHeader(tagmlPath.toFile())

    fun inferHeader(tagmlFile: File): String =
        inferHeader(tagmlFile.readText())

    fun inferHeader(body: String): String =
        body.inferHeader().fold(
            { errors ->
                throw(TAGMLParseError("\nErrors:\n  " + errors.joinToString("\n  ") { pretty(it) }))
            },
            { header -> header }
        )

    private fun pretty(error: ErrorListener.TAGError): String =
        when (error) {
            is ErrorListener.CustomError ->
                "@${error.range.startPosition.line}:${error.range.startPosition.character}..${error.range.endPosition.line}:${error.range.endPosition.character}: ${error.message}"

            else -> error.message
        }

    class TAGMLParseError(message: String) : Throwable(message) {
        override fun getStackTrace(): Array<StackTraceElement> {
            return arrayOf()
        }
    }
}