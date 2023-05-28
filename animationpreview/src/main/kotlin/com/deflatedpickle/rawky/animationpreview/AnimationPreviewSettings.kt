/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.animationpreview

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.rawky.settings.api.range.IntRange
import kotlinx.serialization.Serializable

@Serializable
data class AnimationPreviewSettings(
    override val version: Int = 1,
    @IntRange(1, 240) var speed: Int = 4,
) : Config
