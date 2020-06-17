package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.MultiParent

class Timeline(
    override val children: MutableList<Frame>,
    override var currentChild: Frame
) : MultiParent<Frame>, ChildSelector<Frame>