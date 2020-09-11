package nl.knaw.huygens.tag.jupyter

import nl.knaw.huygens.tag.tagml.TAGMLToken
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.io.File
import java.nio.file.Paths

class JupyterTest {
    @Test
    fun tag_init() {
        TAG.init()
    }

    @Test
    fun tag_initCell() {
        TAG.initCell()
    }

    @Test
    fun tag_shutdown() {
        TAG.shutdown()
    }

    @Test
    fun tokenize_incorrect_tagml_string() {
        val tagml = ("""
            |[!{
            |  ":ontology": {
            |    "root": "tagml"
            |  }
            |}!]
            |[somethingelse>body<somethingelse]
            |""".trimMargin())
        assertParsingFailsWithTAGMLParseError(tagml)
    }

    private fun assertParsingFailsWithTAGMLParseError(tagml: String) {
        try {
            TAG.tokenize(tagml)
            fail("expected TAGMLParseError")
        } catch (e: TAG.TAGMLParseError) {
            println(e.message)
            assertThat(e.message).isNotEmpty
        }
    }

    @Test
    fun tokenize_correct_tagml_path() {
        val tokens: List<TAGMLToken> = TAG.tokenize(Paths.get("data", "good.tagml"))
        assertThat(tokens).hasSize(13)
    }

    @Test
    fun tokenize_incorrect_tagml_file() {
        try {
            TAG.tokenize(File("data", "bad.tagml"))
            fail("expected TAGMLParseError")
        } catch (e: TAG.TAGMLParseError) {
            println(e.message)
            assertThat(e.message).isNotEmpty
        }
    }

}