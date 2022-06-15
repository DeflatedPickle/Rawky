package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.SingleParent
import kotlinx.serialization.Serializable

@Serializable
class Layer(
    override var child: Grid,
    var name: String = "",
) : SingleParent<Grid>