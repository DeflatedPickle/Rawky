/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.shape.api

import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.api.HasRegistry
import com.deflatedpickle.rawky.api.Tool
import javax.swing.ImageIcon

abstract class Shape(
    name: String,
    icon: ImageIcon,
) : Tool(name, icon) {
    companion object : HasRegistry<String, Shape> {
        override val registry = Registry<String, Shape>()
    }
}
