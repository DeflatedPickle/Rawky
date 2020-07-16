package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.MultiParent

class Timeline(
    override val children: Array<Frame>,
    override var selectedIndex: Int
) : MultiParent<Frame>, ChildSelector