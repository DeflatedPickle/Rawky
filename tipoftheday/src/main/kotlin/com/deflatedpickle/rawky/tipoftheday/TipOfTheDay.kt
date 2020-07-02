package com.deflatedpickle.rawky.tipoftheday

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.event.EventWindowShown
import com.deflatedpickle.rawky.tipoftheday.event.EventAddTip
import com.deflatedpickle.rawky.ui.window.Window
import org.jdesktop.swingx.JXTipOfTheDay
import org.jdesktop.swingx.tips.DefaultTipOfTheDayModel
import org.jdesktop.swingx.tips.TipOfTheDayModel

@Plugin(
    value = "tip_of_the_day",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a dialog to show tips other plugins have registered
    """,
    type = PluginType.API,
    dependencies = ["core"]
)
@Suppress("unused")
object TipOfTheDay {
    private val tips = mutableListOf<TipOfTheDayModel.Tip>()

    init {
        EventWindowShown.addListener {
            if (it is Window) {
                EventAddTip.trigger(this.tips)

                if (this.tips.size > 0) {
                    val model = DefaultTipOfTheDayModel(this.tips)
                    val tipOfTheDay = JXTipOfTheDay(model)
                    tipOfTheDay.showDialog(it)
                }
            }
        }

        /*EventAddTip.addListener {
            it.add(object : TipOfTheDayModel.Tip {
                override fun getTipName(): String = "Example"
                override fun getTip(): Any = "You can easily add new tips for plugins"
            })
        }*/
    }
}