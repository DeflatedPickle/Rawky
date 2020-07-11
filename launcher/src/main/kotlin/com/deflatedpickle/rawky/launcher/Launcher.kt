package com.deflatedpickle.rawky.launcher

import com.deflatedpickle.rawky.api.plugin.Plugin

@Plugin(
    value = "launcher",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A basic launcher
    """,
    dependencies = [
        "deflatedpickle;core;1.0.0"
    ]
)
@Suppress("unused")
object Launcher