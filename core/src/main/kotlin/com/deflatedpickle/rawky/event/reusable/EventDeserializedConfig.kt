package com.deflatedpickle.rawky.event.reusable

import com.deflatedpickle.rawky.event.AbstractEvent
import java.io.File

/**
 * Called when a config is deserialized
 */
object EventDeserializedConfig : AbstractEvent<File>()