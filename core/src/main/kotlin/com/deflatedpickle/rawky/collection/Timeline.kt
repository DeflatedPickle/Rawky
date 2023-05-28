/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.MultiParent
import kotlinx.serialization.Serializable

@Serializable
data class Timeline(
    override var selectedIndex: Int = 0,
    override val children: MutableList<Frame> = mutableListOf(),
) : MultiParent<Frame>, ChildSelector
