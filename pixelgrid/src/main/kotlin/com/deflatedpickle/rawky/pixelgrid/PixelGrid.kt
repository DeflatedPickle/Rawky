package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.rawky.api.plugin.Plugin

@Plugin(
    value = "pixel_grid",
    author = "DeflatedPickle",
    description = """
        Provides a grid to draw upon
        
        This plugin provides most of the basic user functionality of Rawky.
    """,
    components = [PixelGridComponent::class]
)
object PixelGrid