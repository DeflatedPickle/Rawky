@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky

import com.deflatedpickle.undulation.serializer.PointSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.awt.Point

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class RawkySettings (
    @Required val cursorSize: @Serializable(PointSerializer::class) Point = Point(32, 32),
)