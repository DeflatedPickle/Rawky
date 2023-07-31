package com.deflatedpickle.rawky.api

import com.deflatedpickle.marvin.registry.Registry
import java.awt.image.BufferedImage
import kotlin.reflect.KClass

abstract class FilterCollection: HasName {
    abstract class Filter : HasName {
        abstract fun filter(
            source: BufferedImage
        ): BufferedImage

        open val category: String? = null
        open val comment: String? = null

        override fun toString() = name
    }

    abstract class ArgumentFilter<T : ArgumentFilter.Packet> : Filter() {
        // TODO: link filters to packets using an annotation
        interface Packet

        data class Range<T : Number>(
            var current : T,
            val min: T,
            val max: T
        )

        abstract val packetClass: KClass<out T>

        abstract fun filter(
            // FIXME: type checking if we use T
            packet: Packet,
            source: BufferedImage
        ): BufferedImage
    }

    companion object :
        HasRegistry<String, FilterCollection>,
        HasCurrent<FilterCollection>,
        HasDefault<FilterCollection> {
        override val registry = Registry<String, FilterCollection>()
        override lateinit var current: FilterCollection
        override lateinit var default: FilterCollection
    }

    abstract val filters: List<Filter>

    override fun toString() = name
}