/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool.bucket

import com.deflatedpickle.haruhi.api.Config
import com.deflatedpickle.rawky.tool.bucket.api.Fill
import com.deflatedpickle.rawky.tool.bucket.serializer.FillSerializer
import kotlinx.serialization.Serializable

@Serializable
data class BucketSettings(
    override val version: Int = 1,
    var fill: @Serializable(FillSerializer::class) Fill? = null
) : Config
