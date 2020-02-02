/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util

import bibliothek.gui.dock.common.CControl
import com.bric.colorpicker.ColorPicker
import com.bric.colorpicker.ColorPickerDialog
import com.deflatedpickle.rawky.api.annotations.Category
import com.deflatedpickle.rawky.api.annotations.Colour
import com.deflatedpickle.rawky.api.annotations.DoubleOpt
import com.deflatedpickle.rawky.api.annotations.Enum
import com.deflatedpickle.rawky.api.annotations.IntOpt
import com.deflatedpickle.rawky.api.annotations.IntRangeOpt
import com.deflatedpickle.rawky.api.annotations.Setter
import com.deflatedpickle.rawky.api.annotations.Tooltip
import com.deflatedpickle.rawky.component.ActionHistory
import com.deflatedpickle.rawky.component.AnimationPreview
import com.deflatedpickle.rawky.component.AnimationTimeline
import com.deflatedpickle.rawky.component.ColourLibrary
import com.deflatedpickle.rawky.component.ColourPalette
import com.deflatedpickle.rawky.component.ColourShades
import com.deflatedpickle.rawky.component.LayerList
import com.deflatedpickle.rawky.component.MiniMap
import com.deflatedpickle.rawky.component.TiledView
import com.deflatedpickle.rawky.component.ToolOptions
import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.component.Window
import com.deflatedpickle.rawky.transfer.ColourTransfer
import com.deflatedpickle.rawky.widget.DoubleSlider
import com.deflatedpickle.rawky.widget.RangeSlider
import com.deflatedpickle.rawky.widget.Slider
import java.awt.Color
import java.awt.Font
import java.awt.GridBagLayout
import java.lang.reflect.Field
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.border.EtchedBorder
import kotlin.math.roundToInt
import org.apache.commons.lang3.StringUtils
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXCollapsiblePane
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.VerticalLayout
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

object Components {
    val frame = Window()

    val cControl = CControl(frame)

    val toolbox = Toolbox()
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
        frame.setSize(1400, 800)

        cControl.contentArea.isOpaque = false

        // toolbox.pencilButton.isSelected = true

        colourPicker.color = Color.WHITE
        colourPicker.addColorListener {
            colourShades.colour = colourPicker.color
            colourShades.updateShades()
        }

        colourPicker.transferHandler = ColourTransfer.Import

        animationTimeline.addFrame()
    }

    fun processAnnotations(parent: JPanel, field: Field): JComponent? {
        var label: JLabel? = null
        if (field.annotations.isNotEmpty()) {
            label = JLabel(StringUtils.splitByCharacterTypeCamelCase(field.name.capitalize()).joinToString(" ") + ":")
            parent.add(label, ToolOptions.StickEast)
        }

        loop@ for (annotation in field.annotations) {
            val widget: JComponent = when (annotation) {
                // TODO: Add more argument types
                is IntOpt -> {
                    Slider.IntSliderComponent(annotation.min, annotation.max, field.getInt(null)).apply {
                        with(slider) {
                            value = field.getInt(null)

                            if (annotation.step > 1) {
                                majorTickSpacing = annotation.step
                                minorTickSpacing = annotation.step / 2
                                snapToTicks = true

                                paintTicks = true
                                paintLabels = true
                            }

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
                is DoubleOpt -> {
                    Slider.DoubleSliderComponent(annotation.min, annotation.max, field.getDouble(null)).apply {
                        with(slider as DoubleSlider) {
                            value = (field.getDouble(null) * factor).toInt()

                            addChangeListener {
                                field.set(null, value / this.factor)
                            }

                            addMouseWheelListener {
                                slider.value += (it.wheelRotation * factor).roundToInt()
                                spinner.value = this.doubleValue
                            }
                        }
                    }
                }
                is IntRangeOpt -> {
                    val range = field.get(null) as IntRange
                    RangeSlider.IntRangeSliderComponent(annotation.min, annotation.max, range.first, range.last).apply {
                        with(slider) {
                            addChangeListener {
                                field.set(null, lowValue..highValue)
                            }

                            addMouseWheelListener {
                                if (slider.lowValue + it.wheelRotation in range) {
                                    slider.lowValue += it.wheelRotation
                                }
                                slider.highValue += it.wheelRotation
                                pastSpinner.value = this.lowValue
                                postSpinner.value = this.highValue
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

                            if (annotation.setter != StringUtils.EMPTY) {
                                field.declaringClass.getMethod(annotation.setter).invoke(field.declaringClass.kotlin.objectInstance)
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

            // Second loop, for non-component annotations
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

    fun processAnnotations(parent: JPanel, clazz: Class<*>): JComponent? {
        loop@ for (annotation in clazz.annotations) {
            val widget: JComponent = when (annotation) {
                is Category -> JPanel().apply {
                    val collapsible = object : JXCollapsiblePane() {
                        init {
                            isCollapsed = true

                            val frame = JXPanel().apply {
                                layout = GridBagLayout()
                            }

                            for (field in clazz.declaredFields) {
                                if (field.name != "INSTANCE") {
                                    processAnnotations(frame, field)
                                }
                            }

                            add(JScrollPane(frame).apply {
                                border = BorderFactory.createEmptyBorder()
                            })
                        }
                    }

                    layout = VerticalLayout()
                    border = BorderFactory.createTitledBorder(EtchedBorder(), clazz.simpleName)

                    add(JButton(collapsible.actionMap[JXCollapsiblePane.TOGGLE_ACTION]).apply {
                        text = text.capitalize()
                    })
                    add(collapsible)
                }
                is Metadata -> continue@loop
                else -> JLabel("${annotation.annotationClass.qualifiedName} is unsupported!").apply {
                    font = font.deriveFont(Font.BOLD)
                    foreground = Color.RED
                }
            }
            parent.add(widget, ToolOptions.FillHorizontal)
        }

        return null
    }
}
