package com.deflatedpickle.rawky.tipoftheday.event

import com.deflatedpickle.rawky.event.AbstractEvent
import org.jdesktop.swingx.tips.TipOfTheDayModel

object EventAddTip : AbstractEvent<MutableList<TipOfTheDayModel.Tip>>()