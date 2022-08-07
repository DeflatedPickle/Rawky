package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.haruhi.api.Config
import com.deflatedpickle.marvin.serializer.FileSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class LauncherSettings(
    override val version: Int = 1,
    val history: MutableList<@Serializable(FileSerializer::class) File> = mutableListOf(),
    var historyLength: Int = 6,
) : Config
