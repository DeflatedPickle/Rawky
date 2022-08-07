package com.deflatedpickle.rawky.pixelgrid.setting

import com.deflatedpickle.haruhi.api.Config
import com.deflatedpickle.rawky.pixelgrid.api.Mode
import com.deflatedpickle.rawky.pixelgrid.serializer.ModeSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PixelGridSettings(
    override val version: Int = 1,
    val divide: LineSettings = LineSettings(),
    var mode: @Serializable(ModeSerializer::class) Mode? = null,
) : Config