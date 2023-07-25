/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.setting

import com.deflatedpickle.haruhi.api.util.Document
import com.deflatedpickle.marvin.serializer.NullableFileSerializer
import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.MultiParent
import com.deflatedpickle.rawky.api.template.Guide
import com.deflatedpickle.rawky.collection.Frame
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File

@Serializable
data class RawkyDocument(
    val version: Int = 1,
    var path:
    @Serializable(NullableFileSerializer::class)
    File? = null,
    var rows: Int = -1,
    var columns: Int = -1,
    var cellProvider: String = "",
    override var selectedIndex: Int = 0,
    override val children: MutableList<Frame>,
    var guides: List<Guide> = listOf(),
) : MultiParent<Frame>, ChildSelector, Document {
    @Transient var dirty = true

    fun addFrame(
        name: String? = null,
        index: Int = -1
    ): Frame {
        val frame = Frame(name = name ?: "Frame ${children.size}")

        if (index == -1) {
            children.add(frame)
        } else {
            children.add(index, frame)
        }

        return frame
    }
}
