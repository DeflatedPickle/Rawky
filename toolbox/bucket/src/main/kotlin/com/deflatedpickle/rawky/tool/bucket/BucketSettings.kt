package com.deflatedpickle.rawky.tool.bucket

import com.deflatedpickle.rawky.tool.bucket.api.Fill
import com.deflatedpickle.rawky.tool.bucket.serializer.FillSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class BucketSettings(
    @Required var fill: @Serializable(FillSerializer::class) Fill? = null
)
