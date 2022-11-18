/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.setting

import com.deflatedpickle.haruhi.api.util.Document
import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.MultiParent
import com.deflatedpickle.rawky.api.template.Guide
import com.deflatedpickle.rawky.collection.Frame
import kotlinx.serialization.Serializable

/**
 * Settings for a file
 */
@Suppress("SpellCheckingInspection")
@Serializable
data class RawkyDocument(
    val version: Int = 1,
    var name: String? = null,
    override val children: MutableList<Frame>,
    override var selectedIndex: Int = 0,
    var guides: List<Guide> = listOf(),
) : MultiParent<Frame>, ChildSelector, Document {
    fun addFrame(name: String? = null, index: Int = -1): Frame {
        val frame = Frame(name = name ?: "Frame ${children.size}")

        if (index == -1) {
            children.add(frame)
        } else {
            children.add(index, frame)
        }

        return frame
    }
}
