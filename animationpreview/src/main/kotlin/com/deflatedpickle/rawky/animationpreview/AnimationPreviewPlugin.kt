/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.animationpreview

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
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
import javax.swing.Timer

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
    settings = AnimationPreviewSettings::class,
)
@Suppress("unused")
object AnimationPreviewPlugin {
    private val timer: Timer

    var playing = false
    var currentFrame = 0

    init {
        val fps = 4
        timer =
            Timer(1000 / fps) {
                if (!playing) return@Timer

                RawkyPlugin.document?.let { doc ->
                    if (currentFrame + 1 < doc.children.size) {
                        currentFrame++
                    } else {
                        currentFrame = 0
                    }

                    triggerButtons()
                }

                AnimationPreviewPanel.animationPanel.repaint()
            }

        EventChangeTheme.addListener { AnimationPreviewPanel.updateUIRecursively() }

        EventUpdateGrid.addListener { AnimationPreviewPanel.animationPanel.repaint() }

        EventUpdateCell.addListener { AnimationPreviewPanel.animationPanel.repaint() }

        EventCreateDocument.addListener { triggerButtons() }

        EventNewFrame.addListener { triggerButtons() }

        EventOpenDocument.addListener { triggerButtons() }

        EventProgramFinishSetup.addListener {
            ConfigUtil.getSettings<AnimationPreviewSettings>("deflatedpickle@animation_preview#*")?.let {
                timer.delay = 1000 / it.speed
            }

            timer.start()
        }
    }

    fun triggerButtons() {
        RawkyPlugin.document?.let { doc ->
            AnimationPreviewPanel.rewindButton.isEnabled = currentFrame - 1 >= 0
            AnimationPreviewPanel.backButton.isEnabled = currentFrame - 1 >= 0
            AnimationPreviewPanel.playButton.isEnabled = doc.children.size >= 1
            AnimationPreviewPanel.forwardButton.isEnabled = currentFrame + 1 < doc.children.size
            AnimationPreviewPanel.fastForwardButton.isEnabled = currentFrame + 1 < doc.children.size
        }
    }
}
