package com.deflatedpickle.rawky.util

import com.bric.colorpicker.ColorPicker
import com.bric.colorpicker.ColorPickerDialog
import com.deflatedpickle.rawky.api.*
import com.deflatedpickle.rawky.api.Enum
import com.deflatedpickle.rawky.api.IntRange
import com.deflatedpickle.rawky.component.*
import com.deflatedpickle.rawky.widget.DoubleSlider
import com.deflatedpickle.rawky.widget.Slider
import org.apache.commons.lang3.StringUtils
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import java.awt.Color
import java.awt.Font
import java.lang.reflect.Field
import javax.swing.*

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

    fun processAnnotations(parent: JPanel, field: Field): JComponent? {
        var label: JLabel? = null
        if (field.annotations.isNotEmpty()) {
            label = JLabel(StringUtils.splitByCharacterTypeCamelCase(field.name.capitalize()).joinToString(" ") + ":")
            parent.add(label, ToolOptions.StickEast)
        }

        var setter: String? = null
        loop@ for (annotation in field.annotations) {
            val widget: JComponent = when (annotation) {
                // TODO: Add more argument types
                is Setter -> {
                    setter = annotation.value
                    continue@loop
                }
                is IntRange -> {
                    Slider.IntSliderComponent(annotation.min, annotation.max, field.getInt(null)).apply {
                        with(slider) {
                            value = field.getInt(null)

                            addChangeListener {
                                field.set(null, value)
                            }

                            addMouseWheelListener {
                                slider.value += it.wheelRotation
                                spinner.value = this.value
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

                            addMouseWheelListener {
                                slider.value += it.wheelRotation
                                spinner.value = this.doubleValue
                            }
                        }
                    }
                }
                is Colour -> {
                    JXButton().apply {
                        val mattePainter = MattePainter(field.get(null) as Color)
                        val compoundPainter = CompoundPainter<JXButton>(mattePainter)

                        backgroundPainter = compoundPainter

                        addActionListener {
                            field.set(null, ColorPickerDialog.showDialog(frame, field.get(null) as Color))
                            mattePainter.fillPaint = field.get(null) as Color
                        }
                    }
                }
                is Enum -> {
                    val clazz = Class.forName(annotation.enum)
                    JComboBox<String>(clazz.enumConstants.map { e ->
                        e.toString()
                                .toLowerCase()
                                .split("_")
                                .joinToString(" ") { it.capitalize() }
                    }.toTypedArray()).apply {
                        selectedIndex = (clazz.cast(field.get(null)) as kotlin.Enum<*>).ordinal

                        addActionListener {
                            field.set(null, clazz.enumConstants[this.selectedIndex])

                            field.declaringClass.getMethod(setter).invoke(Window.Settings)
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

            // Second loop, for not-component annotations
            when (annotation) {
                is Tooltip -> {
                    label?.toolTipText = annotation.string
                    widget.toolTipText = annotation.string
                }
            }

            return widget
        }

        return null
    }
}