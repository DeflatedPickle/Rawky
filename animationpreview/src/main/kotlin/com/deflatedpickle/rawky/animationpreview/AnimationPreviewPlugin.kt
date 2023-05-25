/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.animationpreview

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.util.ComponentPosition
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.event.EventNewFrame
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively

@Plugin(
    value = "animation_preview",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A component to watch the current animation
    """,
    type = PluginType.COMPONENT,
    component = AnimationPreviewPanel::class,
    componentVisible = false,
    componentMinimizedPosition = ComponentPosition.WEST,
    settings = AnimationPreviewSettings::class,
)
@Suppress("unused")
object AnimationPreviewPlugin {
    init {
        EventChangeTheme.addListener {
            AnimationPreviewPanel.updateUIRecursively()
        }

        EventUpdateGrid.addListener {
            AnimationPreviewPanel.animationPanel.repaint()
        }

        EventUpdateCell.addListener {
            AnimationPreviewPanel.animationPanel.repaint()
        }

        EventCreateDocument.addListener {
            triggerButtons()
        }

        EventNewFrame.addListener {
            triggerButtons()
        }

        EventOpenDocument.addListener {
            triggerButtons()
        }

        EventProgramFinishSetup.addListener {
            ConfigUtil.getSettings<AnimationPreviewSettings>("deflatedpickle@animation_preview#*")?.let {
                AnimationPreviewPanel.timer.delay = 1000 / it.speed
            }

            AnimationPreviewPanel.timer.start()
        }
    }

    fun triggerButtons() {
        RawkyPlugin.document?.let { doc ->
            AnimationPreviewPanel.rewindButton.isEnabled = AnimationPreviewPanel.currentFrame - 1 >= 0
            AnimationPreviewPanel.backButton.isEnabled = AnimationPreviewPanel.currentFrame - 1 >= 0
            AnimationPreviewPanel.playButton.isEnabled = doc.children.size >= 1
            AnimationPreviewPanel.forwardButton.isEnabled = AnimationPreviewPanel.currentFrame + 1 < doc.children.size
            AnimationPreviewPanel.fastforwardButton.isEnabled = AnimationPreviewPanel.currentFrame + 1 < doc.children.size
        }
    }
}
