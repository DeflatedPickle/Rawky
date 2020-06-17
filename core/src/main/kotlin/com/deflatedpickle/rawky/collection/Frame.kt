package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.MultiParent

class Frame(
    override val children: MutableList<Layer>,
    override var currentChild: Layer
) : MultiParent<Layer>, ChildSelector<Layer>