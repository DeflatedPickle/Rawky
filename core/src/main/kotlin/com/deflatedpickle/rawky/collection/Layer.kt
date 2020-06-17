package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.SingleChild
import com.deflatedpickle.rawky.api.relation.SingleParent

class Layer(
    override var child: SingleChild<Grid>
) : SingleParent<Grid>