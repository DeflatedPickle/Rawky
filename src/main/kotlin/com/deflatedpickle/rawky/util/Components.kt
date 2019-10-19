package com.deflatedpickle.rawky.util

import com.bric.colorpicker.ColorPicker
import com.deflatedpickle.rawky.api.DoubleRange
import com.deflatedpickle.rawky.api.IntRange
import com.deflatedpickle.rawky.api.Tooltip
import com.deflatedpickle.rawky.component.*
import com.deflatedpickle.rawky.widget.DoubleSlider
import com.deflatedpickle.rawky.widget.Slider
import org.reflections.Reflections
import java.awt.Color
import java.awt.Font
import java.lang.reflect.Field
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

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

    fun processAnnotations(parent: JPanel, field: Field) {
        val label = JLabel(field.name.capitalize() + ":")
        parent.add(label)

        loop@ for (annotation in field.annotations) {
            val widget: JComponent = when (annotation) {
                // TODO: Add more argument types
                is IntRange -> {
                    Slider.IntSliderComponent(annotation.min, annotation.max, field.getInt(null)).apply {
                        with(slider) {
                            value = field.getInt(null)

                            addChangeListener {
                                field.set(null, value)
                            }
                        }
                    }
                }
                is DoubleRange -> {
                    Slider.DoubleSliderComponent(annotation.min, annotation.max, field.getDouble(null)).apply {
                        with(slider as DoubleSlider) {
                            value = (field.getDouble(null) * factor).toInt()

                            addChangeListener {
                                field.set(null, value / this.factor)
                            }
                        }
                    }
                }
                // Tooltips require the widget exist first
                is Tooltip -> continue@loop
                else -> JLabel("${annotation.annotationClass.qualifiedName} is unsupported!").apply {
                    font = font.deriveFont(Font.BOLD)
                    foreground = Color.RED
                }
            }
            parent.add(widget, ToolOptions.FillHorizontal)

            when (annotation) {
                is Tooltip -> {
                    label.toolTipText = annotation.string
                    widget.toolTipText = annotation.string
                }
            }
        }
    }
}