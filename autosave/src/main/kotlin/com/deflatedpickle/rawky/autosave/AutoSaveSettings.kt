/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:OptIn(ExperimentalSerializationApi::class)

package com.deflatedpickle.rawky.autosave

import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.marvin.serializer.FileSerializer
import com.deflatedpickle.rawky.autosave.serializer.FileTypeSerializer
import com.deflatedpickle.rawky.autosave.util.FileType
import com.deflatedpickle.rawky.settings.api.range.IntRange
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.io.File

// TODO: Add a file limit
@Serializable
data class AutoSaveSettings(
    override val version: Int = 1,
    var name: String = "untitled",
    var path:
    @Serializable(FileSerializer::class)
    File = File("."),
    var replace: Boolean = false,
    @IntRange(0, 30) var delay: Int = 1,
    var fileType:
    @Serializable(FileTypeSerializer::class)
    FileType? = null,
    var saveOnFocusLost: Boolean = true,
    var ignoreEmpty: Boolean = true,
) : Config
