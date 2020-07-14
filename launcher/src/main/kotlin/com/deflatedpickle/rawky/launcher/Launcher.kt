package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.plugin.PluginType
import com.deflatedpickle.rawky.launcher.config.LauncherSettings

@Plugin(
    value = "launcher",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A basic launcher
    """,
    type = PluginType.LAUNCHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ],
    settings = LauncherSettings::class
)
@Suppress("unused")
object Launcher