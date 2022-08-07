@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky

import com.deflatedpickle.haruhi.api.Config
import com.deflatedpickle.undulation.serializer.PointSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.awt.Point

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class RawkySettings (
    override val version: Int = 1,
    val cursorSize: @Serializable(PointSerializer::class) Point = Point(32, 32),
) : Config