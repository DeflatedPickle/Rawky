/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.bucket.api

import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.tool.bucket.BucketSettings
import java.awt.Color

interface Fill {
    companion object {
        val registry = Registry<String, Fill>()

        init {
            EventProgramFinishSetup.addListener {
                ConfigUtil.getSettings<BucketSettings>("deflatedpickle@bucket#*")?.let {
                    if (it.fill == null) {
                        it.fill = registry.values.first()

                        PluginUtil.slugToPlugin("deflatedpickle@bucket#*")
                            ?.let { plug -> ConfigUtil.serializeConfig(plug) }
                    }
                }
            }
        }
    }

    val name: String

    fun perform(cell: Cell, row: Int, column: Int, shade: Color)
}
