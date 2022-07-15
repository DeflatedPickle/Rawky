@file:Suppress("MemberVisibilityCanBePrivate", "unused", "LeakingThis")

package com.deflatedpickle.rawky.pixelgrid.api

import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.pixelgrid.setting.PixelGridSettings

abstract class Mode(
    val name: String,
    val priority: Int = 0,
) {
    companion object {
        val registry = Registry<String, Mode>()

        init {
            EventProgramFinishSetup.addListener {
                ConfigUtil.getSettings<PixelGridSettings>("deflatedpickle@pixel_grid#*")?.let {
                    if (it.mode == null) {
                        it.mode = registry.values.maxByOrNull { it.priority }!!
                    }

                    it.mode?.apply()
                }
            }
        }
    }

    abstract fun apply()
    abstract fun remove()

    override fun toString() = name
}