package com.deflatedpickle.rawky.event

import com.deflatedpickle.rawky.ui.component.RawkyPanel

/**
 * Called when a dock holding a [RawkyPanel] looses focus
 */
object EventPanelFocusLost : AbstractEvent<RawkyPanel>()