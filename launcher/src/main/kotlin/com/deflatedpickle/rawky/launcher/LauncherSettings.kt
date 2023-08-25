/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.marvin.serializer.FileSerializer
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class LauncherSettings(
    override val version: Int = 1,
    val history: MutableList<
        @Serializable(FileSerializer::class)
        File,
        > = mutableListOf(),
    var historyLength: Int = 6,
) : Config
