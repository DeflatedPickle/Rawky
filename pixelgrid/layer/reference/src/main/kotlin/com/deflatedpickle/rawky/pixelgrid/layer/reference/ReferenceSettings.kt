/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.layer.reference

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.marvin.serializer.NullableFileSerializer
import com.deflatedpickle.rawky.settings.api.range.FloatRange
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class ReferenceSettings(
    override val version: Int = 1,
    var enabled: Boolean = true,
    var file: @Serializable(NullableFileSerializer::class) File? = null,
    @FloatRange(0f, 1f) var opacity: Float = 0.5f,
) : Config
