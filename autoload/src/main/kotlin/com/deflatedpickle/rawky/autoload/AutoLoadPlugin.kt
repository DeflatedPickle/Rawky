/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.autoload

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventSaveDocument
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.autoload.api.LoadType.LAST_OPENED
import com.deflatedpickle.rawky.autoload.api.LoadType.LAST_SAVED
import com.deflatedpickle.rawky.autosave.event.EventAutoSaveDocument
import com.deflatedpickle.rawky.launcher.LauncherPlugin
import com.deflatedpickle.rawky.toolbox.event.EventToolboxFinish

@Plugin(
    value = "auto_load",
    author = "DeflatedPickle",
    version = "1.0.0",
    description =
    """
        <br>
        Causes the last open or saved file to be loaded when the program is opened
    """,
    dependencies =
    [
        "deflatedpickle@core#*",
        "deflatedpickle@launcher#*",
        "deflatedpickle@toolbox#*",
    ],
    settings = AutoLoadSettings::class,
)
@Suppress("unused")
object AutoLoadPlugin {
    init {
        EventOpenDocument.addListener { open ->
            ConfigUtil.getSettings<AutoLoadSettings>("deflatedpickle@auto_load#*")?.let {
                if (it.loadType == LAST_OPENED) {
                    it.lastFile = open.second

                    PluginUtil.slugToPlugin("deflatedpickle@auto_load#*")?.let { plug ->
                        ConfigUtil.serializeConfig(plug)
                    }
                }
            }
        }

        EventSaveDocument.addListener { save ->
            ConfigUtil.getSettings<AutoLoadSettings>("deflatedpickle@auto_load#*")?.let {
                if (it.loadType == LAST_SAVED) {
                    it.lastFile = save.second

                    PluginUtil.slugToPlugin("deflatedpickle@auto_load#*")?.let { plug ->
                        ConfigUtil.serializeConfig(plug)
                    }
                }
            }
        }

        EventAutoSaveDocument.addListener { save ->
            ConfigUtil.getSettings<AutoLoadSettings>("deflatedpickle@auto_load#*")?.let {
                if (it.loadType == LAST_SAVED && it.includeAutoSaves) {
                    it.lastFile = save.second

                    PluginUtil.slugToPlugin("deflatedpickle@auto_load#*")?.let { plug ->
                        ConfigUtil.serializeConfig(plug)
                    }
                }
            }
        }

        EventToolboxFinish.addListener {
            ConfigUtil.getSettings<AutoLoadSettings>("deflatedpickle@auto_load#*")?.let {
                it.lastFile?.let { file ->
                    if (file.exists()) {
                        LauncherPlugin.open(file)
                    } else {
                        it.lastFile = null

                        PluginUtil.slugToPlugin("deflatedpickle@auto_load#*")?.let { plug ->
                            ConfigUtil.serializeConfig(plug)
                        }
                    }
                }
            }
        }
    }
}
