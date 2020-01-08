/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.annotations.RedrawSensitive
import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.util.Components
import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel
import javax.swing.Timer

@RedrawSensitive<PixelGrid>(PixelGrid::class)
class AnimationPreview : Component() {
    val spinner = JSpinner(SpinnerNumberModel(1, 1, 240, 1)).apply {
        val timer = Timer(1000 / this.value as Int) {
            if (Components.animationPreview.frame < Components.animationTimeline.listModel.size() - 1) {
                Components.animationPreview.frame++

                Components.animationPreview.repaintWithChildren()
            } else {
                Components.animationPreview.frame = 0

                Components.animationPreview.repaintWithChildren()
            }
        }.apply { start() }

        addChangeListener {
            timer.delay = 1000 / this.value as Int
        }
    }

    init {
        toolbarWidgets[BorderLayout.PAGE_END] = listOf(spinner)
    }

    var frame = 0

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D
        g2D.scale(0.5, 0.5)

        PixelGrid.drawTransparentBackground(g2D, fillType = PixelGrid.FillType.ALL, backgroundPixelDivider = 42)

        for ((layerIndex, layer) in PixelGrid.frameList[frame].layerList.withIndex().reversed()) {
            PixelGrid.drawPixels(layerIndex, layer, g2D)
        }
    }
}
