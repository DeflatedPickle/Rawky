/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api

import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.event.EventChangeTool
import javax.swing.ImageIcon

abstract class Tool<T>(
    override val name: String,
    val icon: ImageIcon,
) : HasName {
    companion object : HasRegistry<String, Tool<*>>, HasCurrent<Tool<*>> {
        override val registry = Registry<String, Tool<*>>()

        const val defaultSize = 32
        override lateinit var current: Tool<*>

        init {
            EventCreateDocument.addListener {
                current = registry.values.first()
                EventChangeTool.trigger(current)
            }

            EventOpenDocument.addListener {
                current = registry.values.first()
                EventChangeTool.trigger(current)
            }
        }

        fun isToolValid() = this::current.isInitialized
    }

    abstract fun perform(
        cell: Cell<Any>,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    )

    abstract fun getSettings(): T?
    open fun getQuickSettings(): List<String> = listOf()

    override fun toString() = name
}
