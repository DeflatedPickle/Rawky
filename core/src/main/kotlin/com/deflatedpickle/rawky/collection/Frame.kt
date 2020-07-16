package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.MultiParent
import kotlinx.serialization.Serializable

@Serializable
class Frame(
    override val children: Array<Layer>,
    override var selectedIndex: Int = 0
) : MultiParent<Layer>, ChildSelector