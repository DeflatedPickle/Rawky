/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.setting

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.rawky.pixelgrid.api.Mode
import com.deflatedpickle.rawky.pixelgrid.serializer.ModeSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PixelGridSettings(
    override val version: Int = 1,
    val divide: LineSettings = LineSettings(),
    var mode: @Serializable(ModeSerializer::class) Mode? = null,
    val guide: GuideSettings = GuideSettings(),
) : Config
