package com.deflatedpickle.rawky.util

import khttp.get
import khttp.responses.Response
import org.json.JSONObject
import java.awt.Color

object ColourAPI {
    enum class EndPoint {
        ID,
        // TODO: Generate colour palettes with this
        SCHEME
    }

    enum class ColourType {
        // Other formats don't need to be supported
        // But they could be...
        RGB
    }

    private const val url = "https://www.thecolorapi.com"

    // TODO: Save/load the colour cache from a file
    val cache = mutableMapOf<
            Triple<Int, Int, Int>,
            JSONObject
            >()

    fun request(type: EndPoint, colourType: ColourType, packet: String): Response = get("$url/${type.name.toLowerCase()}?${colourType.name.toLowerCase()}=$packet")

    fun id(r: Int, g: Int, b: Int): JSONObject {
        with(Triple(r, g, b)) {
            if (cache.containsKey(this)) {
                return cache[this]!!
            } else {
                with(request(EndPoint.ID, ColourType.RGB, "$r,$g,$b")) {
                    cache[Triple(r, g, b)] = jsonObject
                    return jsonObject
                }
            }
        }
    }

    fun id(colour: Color): JSONObject = this.id(colour.red, colour.green, colour.blue)
}