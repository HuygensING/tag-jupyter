package nl.knaw.huygens.tag.jupyter

import nl.knaw.huygens.graphviz.DotEngine
import nl.knaw.huygens.tag.mct.DotFactory
import nl.knaw.huygens.tag.mct.TAGMCT

private val dotEngine = DotEngine()

fun TAGMCT.asSVGPair(): Pair<String, String> =
    Pair(
        "image/svg+xml",
        dotEngine.renderAs(
            "svg",
            DotFactory.fromTAGMCT(this)
        )
    )