package com.deflatedpickle.rawky.event.specific

import com.deflatedpickle.rawky.event.AbstractEvent
import javax.swing.JFrame

/**
 * Called when a window is shown
 */
object EventWindowShown : AbstractEvent<JFrame>()