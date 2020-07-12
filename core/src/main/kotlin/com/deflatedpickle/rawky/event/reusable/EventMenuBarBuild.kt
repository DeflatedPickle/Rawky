package com.deflatedpickle.rawky.event.reusable

import com.deflatedpickle.rawky.event.AbstractEvent
import javax.swing.JMenuBar

/**
 * Called after a menu bar has been created
 */
object EventMenuBarBuild : AbstractEvent<JMenuBar>()