package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.MultiParent
import kotlinx.serialization.Serializable

@Serializable
data class Timeline(
    override val children: MutableList<Frame> = mutableListOf(),
    override var selectedIndex: Int = 0,
) : MultiParent<Frame>, ChildSelector