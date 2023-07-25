/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.autoload

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.marvin.serializer.NullableFileSerializer
import com.deflatedpickle.rawky.autoload.api.LoadType
import com.deflatedpickle.rawky.autoload.api.LoadType.LAST_SAVED
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class AutoLoadSettings(
    override val version: Int = 1,
    var lastFile:
    @Serializable(NullableFileSerializer::class)
    File? = null,
    var loadType: LoadType = LAST_SAVED,
    var includeAutoSaves: Boolean = true,
) : Config
