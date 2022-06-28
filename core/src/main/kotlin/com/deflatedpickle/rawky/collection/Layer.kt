package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.SingleParent
import kotlinx.serialization.Serializable

@Serializable
data class Layer(
    override var child: Grid = Grid(),
    var name: String = "",
) : SingleParent<Grid>