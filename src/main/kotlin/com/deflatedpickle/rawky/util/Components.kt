package com.deflatedpickle.rawky.util

import com.bric.colorpicker.ColorPicker
import com.deflatedpickle.rawky.component.*
import org.reflections.Reflections
import java.awt.Color
import javax.swing.JFrame

object Components {
    val frame = Window()

    val toolbox = Toolbox()
    val pixelGrid = PixelGrid.INSTANCE
    val tiledView = TiledView()
    val colourPicker = ColorPicker(false, true)
    val colourShades = ColourShades()
    val colourLibrary = ColourLibrary()
    val colourPalette = ColourPalette()
    val layerList = LayerList()
    val animationTimeline = AnimationTimeline()
    val animationPreview = AnimationPreview()
    val actionHistory = ActionHistory()
    val toolOptions = ToolOptions()
    val miniMap = MiniMap()

    init {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(1200, 800)

        // toolbox.pencilButton.isSelected = true

        colourPicker.color = Color.WHITE
        colourPicker.addColorListener {
            colourShades.colour = colourPicker.color
            colourShades.updateShades()
        }

        animationTimeline.addFrame()
    }
}