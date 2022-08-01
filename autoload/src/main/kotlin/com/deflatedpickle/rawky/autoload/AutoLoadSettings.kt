/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.autoload

import com.deflatedpickle.marvin.serializer.NullableFileSerializer
import com.deflatedpickle.rawky.autoload.api.LoadType
import com.deflatedpickle.rawky.autoload.api.LoadType.LAST_SAVED
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AutoLoadSettings(
    @Required var lastFile: @Serializable(NullableFileSerializer::class) File? = null,
    @Required var loadType: LoadType = LAST_SAVED,
    @Required var includeAutoSaves: Boolean = true,
)
