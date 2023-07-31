package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort

import com.deflatedpickle.rawky.api.FilterCollection
import com.jhlabs.image.DisplaceFilter
import com.jhlabs.image.DissolveFilter
import com.jhlabs.image.FieldWarpFilter
import com.jhlabs.image.KaleidoscopeFilter
import com.jhlabs.image.MarbleFilter
import com.jhlabs.image.MirrorFilter
import com.jhlabs.image.PerspectiveFilter
import java.awt.image.BufferedImage

object Perspective : FilterCollection.Filter() {
    override val name = "Perspective"
    override val category = "Distort"
    override val comment = "Perspective distortion"

    override fun filter(
        source: BufferedImage
    ): BufferedImage = PerspectiveFilter().filter(source, null)
}