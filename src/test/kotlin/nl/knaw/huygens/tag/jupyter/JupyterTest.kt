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

    @Test
    fun infer_header_from_string() {
        val body = "[root>text<root]"
        val header = TAG.inferHeader(body)
        val expectedHeader = """
            |[!{
            |  ":ontology": {
            |    "root": "root",
            |    "elements": {
            |      "root": {
            |        "description": "..."
            |      }
            |    },
            |    "attributes": {}
            |  }
            |}!]""".trimMargin()
        assertThat(header).isEqualTo(expectedHeader)
    }

    @Test
    fun infer_header_from_file() {
        val file = File("data/body.tagml")
        val header = TAG.inferHeader(file)
        val expectedHeader = """
            |[!{
            |  ":ontology": {
            |    "root": "q",
            |    "elements": {
            |      "q": {
            |        "description": "..."
            |      }
            |    },
            |    "attributes": {}
            |  }
            |}!]""".trimMargin()
        assertThat(header).isEqualTo(expectedHeader)
    }

    @Test
    fun infer_header_from_path() {
        val file = Paths.get("data", "body.tagml")
        val header = TAG.inferHeader(file)
        val expectedHeader = """
            |[!{
            |  ":ontology": {
            |    "root": "q",
            |    "elements": {
            |      "q": {
            |        "description": "..."
            |      }
            |    },
            |    "attributes": {}
            |  }
            |}!]""".trimMargin()
        assertThat(header).isEqualTo(expectedHeader)
    }

    @Test
    fun infer_header_from_bad_body_file() {
        val file = File("data/bad_body.tagml")
        try {
            val header = TAG.inferHeader(file)
            fail("expected exception")
        } catch (e: TAG.TAGMLParseError) {
            assertThat(e.message).isEqualTo(
                """
                |Errors:
                |  @8:31..8:36: Closing tag "<end]" found without corresponding open tag.
                |  @8:36..8:48: Unexpected closing tag: found <dummy_root], but expected <start]""".trimMargin()
            )
        }
    }

}