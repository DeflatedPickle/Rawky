/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.MultiParent
import kotlinx.serialization.Serializable

@Serializable
data class Frame(
    var name: String = "",
    override var selectedIndex: Int = 0,
    override val children: MutableList<Layer> = mutableListOf(),
) : MultiParent<Layer>, ChildSelector {
    fun addLayer(
        name: String? = null,
        rows: Int,
        columns: Int,
        /*index: Int = -1*/
    ): Layer {
        val layer =
            Layer(name = name ?: "Layer ${children.size}", child = Grid(rows, columns)).apply {
                frame = this@Frame
            }

        children.add(layer)

        return layer
    }

    operator fun get(index: Int) = children[index]
    operator fun iterator() = children.iterator()

    override fun toString() = name
}
