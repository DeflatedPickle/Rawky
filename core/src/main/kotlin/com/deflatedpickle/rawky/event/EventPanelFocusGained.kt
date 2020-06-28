package com.deflatedpickle.rawky.event

import com.deflatedpickle.rawky.ui.component.RawkyPanel

/**
 * Called when a dock holding a [RawkyPanel] receives focus
 */
object EventPanelFocusGained : AbstractEvent<RawkyPanel>()