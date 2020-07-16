package com.deflatedpickle.rawky.event.specific

import com.deflatedpickle.rawky.event.AbstractEvent
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.ui.component.RawkyPanel
import javax.swing.JFrame

/**
 * Called when a window is shown
 */
object EventCreatedPluginComponents : AbstractEvent<List<RawkyPanel>>()