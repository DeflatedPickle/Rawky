package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.MultiParent
import kotlinx.serialization.Serializable

@Serializable
data class Frame(
    override val children: MutableList<Layer> = mutableListOf(),
    override var selectedIndex: Int = 0,
    var name: String = "",
) : MultiParent<Layer>, ChildSelector {
    fun addLayer(name: String? = null, rows: Int, columns: Int, /*index: Int = -1*/): Layer {
        val layer = Layer(Grid(rows, columns), name = name ?: "Layer ${children.size}")
            .apply { frame = this@Frame }

        children.add(layer)

        return layer
    }

    override fun toString() = name
}