/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.animationpreview

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.util.DrawUtil
import com.deflatedpickle.undulation.api.ButtonType
import com.deflatedpickle.undulation.functions.AbstractButton
import org.jdesktop.swingx.JXPanel
import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JToolBar

object AnimationPreviewPanel : PluginPanel() {
    val animationPanel =
        object : JXPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)

                val g2D = g as Graphics2D
                val bufferedImage =
                    BufferedImage(
                        visibleRect.x + visibleRect.width,
                        visibleRect.y + visibleRect.height,
                        BufferedImage.TYPE_INT_ARGB,
                    )

                RawkyPlugin.document?.let { doc ->
                    for (
                    v in
                    PaintLayer.registry.getAll().values.filter {
                        it.layer == LayerCategory.GRID || it.layer == LayerCategory.BACKGROUND
                    }
                    ) {
                        val temp = bufferedImage.createGraphics()

                        val factor = DrawUtil.getScaleFactor(
                            width.toDouble() / Grid.pixel, height.toDouble() / Grid.pixel,
                            doc.columns.toDouble(), doc.rows.toDouble()
                        )
                        temp.scale(factor, factor)

                        doc.children[AnimationPreviewPlugin.currentFrame].let { frame ->
                            for (layer in frame.children.indices) {
                                v.paint(doc, doc.selectedIndex, layer, temp)
                                temp.dispose()
                            }
                        }
                    }
                }

                g2D.drawRenderedImage(bufferedImage, null)
            }
        }

    val rewindButton =
        AbstractButton(icon = MonoIcon.REWIND, tooltip = "Rewind", enabled = false) {
            RawkyPlugin.document?.let {
                AnimationPreviewPlugin.currentFrame = 0
                animationPanel.repaint()

                AnimationPreviewPlugin.triggerButtons()
            }
        }

    val backButton =
        AbstractButton(icon = MonoIcon.ARROW_LEFT, tooltip = "Back", enabled = false) {
            RawkyPlugin.document?.let {
                if (AnimationPreviewPlugin.currentFrame - 1 >= 0) {
                    AnimationPreviewPlugin.currentFrame--
                    animationPanel.repaint()
                }

                AnimationPreviewPlugin.triggerButtons()
            }
        }

    val forwardButton =
        AbstractButton(icon = MonoIcon.ARROW_RIGHT, tooltip = "Forward", enabled = false) {
            RawkyPlugin.document?.let { doc ->
                if (AnimationPreviewPlugin.currentFrame + 1 < doc.children.size) {
                    AnimationPreviewPlugin.currentFrame++
                    animationPanel.repaint()
                }

                AnimationPreviewPlugin.triggerButtons()
            }
        }

    val playButton =
        AbstractButton(
            icon = MonoIcon.RUN,
            tooltip = "Play",
            enabled = false,
            type = ButtonType.TOGGLE,
        ) {
            AnimationPreviewPlugin.playing = it.isSelected
            animationPanel.repaint()

            it.icon =
                if (!it.isSelected) {
                    MonoIcon.RUN
                } else {
                    MonoIcon.PAUSE
                }
        }

    val fastforwardButton =
        AbstractButton(icon = MonoIcon.FAST_FORWARD, tooltip = "Fast Forward", enabled = false) {
            RawkyPlugin.document?.let { doc ->
                AnimationPreviewPlugin.currentFrame = doc.children.size - 1
                animationPanel.repaint()

                AnimationPreviewPlugin.triggerButtons()
            }
        }

    private val navbar =
        JToolBar("Navbar").apply {
            add(rewindButton)
            add(backButton)
            add(playButton)
            add(forwardButton)
            add(fastforwardButton)
        }

    init {
        layout = BorderLayout()

        add(animationPanel, BorderLayout.CENTER)
        add(navbar, BorderLayout.SOUTH)
    }
}
