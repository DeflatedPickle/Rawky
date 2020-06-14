package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType

@Plugin(
    value = "pixel_grid",
    author = "DeflatedPickle",
    description = """
        Provides a grid to draw upon
        <br>
        This plugin provides most of the basic user functionality of Rawky.
    """,
    type = PluginType.COMPONENT,
    components = [PixelGridComponent::class]
)
object PixelGrid