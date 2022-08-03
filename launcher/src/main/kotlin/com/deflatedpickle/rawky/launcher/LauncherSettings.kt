package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.marvin.serializer.FileSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class LauncherSettings(
    @Required val history: MutableList<@Serializable(FileSerializer::class) File> = mutableListOf(),
    @Required var historyLength: Int = 6,
)
