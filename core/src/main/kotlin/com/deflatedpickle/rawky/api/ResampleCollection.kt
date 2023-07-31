package com.deflatedpickle.rawky.api

import com.deflatedpickle.marvin.registry.Registry
import java.awt.image.BufferedImage

abstract class ResampleCollection: HasName {
    abstract class Resampler : HasName {
        abstract fun resample(
            width: Int,
            height: Int,
            source: BufferedImage
        ): BufferedImage

        override fun toString() = name
    }

    companion object :
        HasRegistry<String, ResampleCollection>,
        HasCurrent<ResampleCollection>,
        HasDefault<ResampleCollection> {
        override val registry = Registry<String, ResampleCollection>()
        override lateinit var current: ResampleCollection
        override lateinit var default: ResampleCollection
    }

    abstract val resamplers: List<Resampler>

    override fun toString() = name
}