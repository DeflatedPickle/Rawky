/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused", "UNUSED_PARAMETER")

package com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.api.ResampleCollection
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Blackman
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.BlackmanBessel
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.BlackmanSinc
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Box
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Catrom
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Cubic
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Gaussian
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Hamming
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Hanning
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Hermite
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Lanczos
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Mitchell
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Point
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Quadratic
import com.deflatedpickle.rawky.pixelgrid.resample.twelvemonkeys.resample.Triangle

@Plugin(
    value = "twelve_monkeys_resample",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Resample images using the TwelveMonkeys library
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@pixel_grid#*",
    ],
)
object TwelveMonkeysResamplePlugin : ResampleCollection() {
    override val name = "TwelveMonkeys"
    override val resamplers = listOf(
        Point,
        Box,
        Triangle,
        Hermite,
        Hanning,
        Hamming,
        Blackman,
        Gaussian,
        Quadratic,
        Cubic,
        Catrom,
        Mitchell,
        Lanczos,
        BlackmanBessel,
        BlackmanSinc,
    )

    init {
        registry[name] = this
        current = this
        default = this
    }
}
