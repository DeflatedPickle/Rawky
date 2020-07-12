package com.deflatedpickle.rawky.event.reusable

import com.deflatedpickle.rawky.event.AbstractEvent
import java.io.File

/**
 * Called when a file is created
 */
object EventCreateFile : AbstractEvent<File>()