@file:OptIn(ExperimentalSerializationApi::class)

package com.deflatedpickle.rawky.autosave

import com.deflatedpickle.marvin.serializer.FileSerializer
import com.deflatedpickle.rawky.autosave.serializer.FileTypeSerializer
import com.deflatedpickle.rawky.autosave.util.FileType
import com.deflatedpickle.rawky.settings.api.IntRange
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class AutoSaveSettings(
    @Required var name: String = "untitled",
    @Required var path: @Serializable(FileSerializer::class) File = File("."),
    @Required var replace: Boolean = false,
    @Required @IntRange(0, 30) var delay: Int = 1,
    @Required var fileType: @Serializable(FileTypeSerializer::class) FileType? = null,
)