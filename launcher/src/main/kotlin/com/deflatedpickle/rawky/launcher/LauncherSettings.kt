package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.marvin.serializer.FileSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class LauncherSettings(
    val history: MutableList<@Serializable(FileSerializer::class) File> = mutableListOf(),
    var historyLength: Int = 6,
)