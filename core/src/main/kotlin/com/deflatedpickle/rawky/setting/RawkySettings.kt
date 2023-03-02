/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.setting

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.undulation.serializer.PointSerializer
import kotlinx.serialization.Serializable
import java.awt.Point

@Serializable
data class RawkySettings(
    override val version: Int = 1,
    var debug: DebugSettings = DebugSettings(),
    val cursorSize: @Serializable(PointSerializer::class) Point = Point(32, 32),
) : Config
