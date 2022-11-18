/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.autosave.event

import com.deflatedpickle.haruhi.api.event.AbstractEvent
import com.deflatedpickle.haruhi.api.util.Document
import java.io.File

@Suppress("unused")
object EventAutoSaveDocument : AbstractEvent<Pair<Document, File>>()
