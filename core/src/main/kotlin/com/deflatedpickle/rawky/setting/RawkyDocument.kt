package com.deflatedpickle.rawky.setting

import com.deflatedpickle.haruhi.api.util.Document
import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.MultiParent
import com.deflatedpickle.rawky.collection.Frame
import kotlinx.serialization.Serializable

/**
 * Settings for a file
 */
@Suppress("SpellCheckingInspection")
@Serializable
class RawkyDocument(
    override val children: Array<Frame>,
    override var selectedIndex: Int = 0
) : MultiParent<Frame>, ChildSelector, Document