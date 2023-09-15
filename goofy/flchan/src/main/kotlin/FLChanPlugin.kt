import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.rawky.launcher.gui.Window
import com.deflatedpickle.undulation.functions.extensions.add
import javax.swing.JMenu

@Plugin(
    value = "flchan",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Spawn FL Chan
    """,
    type = PluginType.OTHER,
    settings = FLChanSettings::class
)
object FLChanPlugin {
    init {
        EventProgramFinishSetup.addListener {
            ConfigUtil.getSettings<FLChanSettings>("deflatedpickle@flchan#*")?.let { settings ->
                RegistryUtil.get(MenuCategory.MENU.name)?.apply {
                    (get(MenuCategory.TOOLS.name) as JMenu).apply {
                        (menuComponents.filterIsInstance<JMenu>().firstOrNull { it.text == "Goofy" }
                            ?: JMenu("Goofy").also { add(it) }).apply {
                            add("FLChan") {
                                FLChan(Window, settings.action, settings.reflectOpacity)
                            }
                        }
                    }
                }
            }
        }
    }
}