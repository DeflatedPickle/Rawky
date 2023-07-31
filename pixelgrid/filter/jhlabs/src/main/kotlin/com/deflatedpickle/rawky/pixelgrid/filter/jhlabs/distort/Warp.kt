package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DisplaceFilter
import com.jhlabs.image.DissolveFilter
import com.jhlabs.image.FieldWarpFilter
import com.jhlabs.image.KaleidoscopeFilter
import com.jhlabs.image.MarbleFilter
import com.jhlabs.image.MirrorFilter
import com.jhlabs.image.PerspectiveFilter
import com.jhlabs.image.PinchFilter
import com.jhlabs.image.PolarFilter
import com.jhlabs.image.RippleFilter
import com.jhlabs.image.ShearFilter
import com.jhlabs.image.SphereFilter
import com.jhlabs.image.SwimFilter
import com.jhlabs.image.TwirlFilter
import com.jhlabs.image.WarpFilter
import java.awt.image.BufferedImage

object Warp : FilterCollection.Filter() {
    override val name = "Warp"
    override val category = "Distort"
    override val comment = "A general grid image warp"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = WarpFilter().filter(source, null)
}