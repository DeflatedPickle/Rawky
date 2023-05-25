/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.animationpreview

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.undulation.api.ButtonType
import com.deflatedpickle.undulation.functions.AbstractButton
import com.deflatedpickle.undulation.functions.extensions.add
import org.jdesktop.swingx.JXPanel
import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.AbstractButton
import javax.swing.JToolBar
import javax.swing.Timer

object AnimationPreviewPanel : PluginPanel() {
    val timer: Timer

    var playing = false
    var currentFrame = 0

    val animationPanel = object : JXPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            val g2d = g as Graphics2D
            val bufferedImage = BufferedImage(
                visibleRect.x + visibleRect.width,
                visibleRect.y + visibleRect.height,
                BufferedImage.TYPE_INT_ARGB
            )

            for (v in PaintLayer.registry.getAll().values.filter { it.layer == LayerCategory.GRID || it.layer == LayerCategory.BACKGROUND }) {
                val temp = bufferedImage.createGraphics()

                RawkyPlugin.document?.let { doc ->
                    doc.children[currentFrame].let { frame ->
                        for (layer in frame.children) {
                            v.paint(doc, frame, layer, temp)
                            temp.dispose()
                        }
                    }
                }
            }

            g2d.drawRenderedImage(bufferedImage, null)
        }
    }

    val rewindButton = AbstractButton(icon = MonoIcon.REWIND, tooltip = "Rewind", enabled = false) {
        RawkyPlugin.document?.let {
            currentFrame = 0
            animationPanel.repaint()

            AnimationPreviewPlugin.triggerButtons()
        }
    }

    val backButton = AbstractButton(icon = MonoIcon.ARROW_LEFT, tooltip = "Back", enabled = false) {
        RawkyPlugin.document?.let {
            if (currentFrame - 1 >= 0) {
                currentFrame--
                animationPanel.repaint()
            }

            AnimationPreviewPlugin.triggerButtons()
        }
    }

    val forwardButton = AbstractButton(icon = MonoIcon.ARROW_RIGHT, tooltip = "Forward", enabled = false) {
        RawkyPlugin.document?.let { doc ->
            if (currentFrame + 1 < doc.children.size) {
                currentFrame++
                animationPanel.repaint()
            }

            AnimationPreviewPlugin.triggerButtons()
        }
    }

    val playButton = AbstractButton(icon = MonoIcon.RUN, tooltip = "Play", enabled = false, type = ButtonType.TOGGLE) {
        playing = it.isSelected
        animationPanel.repaint()

        it.icon = if (!it.isSelected) {
            MonoIcon.RUN
        } else {
            MonoIcon.PAUSE
        }
    }

    val fastforwardButton = AbstractButton(icon = MonoIcon.FAST_FORWARD, tooltip = "Fast Forward", enabled = false) {
        RawkyPlugin.document?.let { doc ->
            currentFrame = doc.children.size - 1
            animationPanel.repaint()

            AnimationPreviewPlugin.triggerButtons()
        }
    }

    val navbar = JToolBar("Navbar").apply {
        add(rewindButton)
        add(backButton)
        add(playButton)
        add(forwardButton)
        add(fastforwardButton)
    }

    init {
        layout = BorderLayout()

        val fps = 4

        timer = Timer(1000 / fps) {
            if (!playing) return@Timer

            RawkyPlugin.document?.let { doc ->
                if (currentFrame + 1 < doc.children.size) {
                    currentFrame++
                } else {
                    currentFrame = 0
                }

                AnimationPreviewPlugin.triggerButtons()
            }

            animationPanel.repaint()
        }

        add(animationPanel, BorderLayout.CENTER)
        add(navbar, BorderLayout.SOUTH)
    }
}
