package com.deflatedpickle.rawky.event.specific

import com.deflatedpickle.rawky.event.AbstractEvent
import com.deflatedpickle.tosuto.ToastWindow

/**
 * Called when the toast window is shown
 */
object EventToastWindowShown : AbstractEvent<ToastWindow>()