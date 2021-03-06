/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import com.bric.colorpicker.ColorPicker
import com.bric.colorpicker.ColorPickerDialog
import com.bric.colorpicker.ColorPickerPanel
import com.deflatedpickle.rawky.api.annotations.Category
import com.deflatedpickle.rawky.api.annotations.Colour
import com.deflatedpickle.rawky.api.annotations.DoubleOpt
import com.deflatedpickle.rawky.api.annotations.Enum
import com.deflatedpickle.rawky.api.annotations.IntOpt
import com.deflatedpickle.rawky.api.annotations.IntRangeOpt
import com.deflatedpickle.rawky.api.annotations.Toggle
import com.deflatedpickle.rawky.api.annotations.Tooltip
import com.deflatedpickle.rawky.component.ActionHistory
import com.deflatedpickle.rawky.component.AnimationPreview
import com.deflatedpickle.rawky.component.AnimationTimeline
import com.deflatedpickle.rawky.component.ColourLibrary
import com.deflatedpickle.rawky.component.ColourPalette
import com.deflatedpickle.rawky.component.ColourShades
import com.deflatedpickle.rawky.component.LayerList
import com.deflatedpickle.rawky.component.MiniMap
import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.component.TiledView
import com.deflatedpickle.rawky.component.ToolOptions
import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.component.Window
import com.deflatedpickle.rawky.transfer.ColourTransfer
import com.deflatedpickle.rawky.util.extension.toCamelCase
import com.deflatedpickle.rawky.widget.DoubleSlider
import com.deflatedpickle.rawky.widget.RangeSlider
import com.deflatedpickle.rawky.widget.Slider
import java.awt.Color
import java.awt.Component
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
import org.jdesktop.swingx.painter.CheckerboardPainter
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.jvm.isAccessible

object Components {
    val frame = Window()

    val cControl = CControl(frame)
    val grid = CGrid(cControl)

    val toolbox = Toolbox()
    val tiledView = TiledView()
    val colourPicker = ColorPicker(false, true)
    val colourShades = ColourShades()
    val colourLibrary = ColourLibrary()
    val colourPalette = ColourPalette()
    val layerList = LayerList()
    val animationTimeline = AnimationTimeline()
    val animationPreview = AnimationPreview()
    val toolOptions = ToolOptions()
    val miniMap = MiniMap()

    fun componentToInstance(component: EComponent): Component = when (component) {
        EComponent.FRAME -> frame
        EComponent.TOOLBOX -> toolbox
        EComponent.PIXEL_GRID -> PixelGrid
        EComponent.TILED_VIEW -> tiledView
        EComponent.COLOUR_PICKER -> colourPicker
        EComponent.COLOUR_SHADES -> colourShades
        EComponent.COLOUR_LIBRARY -> colourLibrary
        EComponent.COLOUR_PALETTE -> colourPalette
        EComponent.LAYER_LIST -> layerList
        EComponent.ANIMATION_TIMELINE -> animationTimeline
        EComponent.ANIMATION_PREVIEW -> animationPreview
        EComponent.ACTION_HISTORY -> ActionHistory
        EComponent.TOOL_OPTIONS -> toolOptions
        EComponent.MINI_MAP -> miniMap
    }

    init {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(1400, 800)

        cControl.contentArea.isOpaque = false

        // toolbox.pencilButton.isSelected = true

        colourPicker.color = Color.WHITE

        // They define the colour picker part as private...
        // So we have to reflect it
        with(colourPicker::class.declaredMembers.single { it.name == "colorPanel" }) {
            isAccessible = true
            (this.call(colourPicker) as ColorPickerPanel).addMouseListener(object : MouseAdapter() {
                override fun mouseReleased(e: MouseEvent) {
                    colourShades.updateShades()
                    // This fires twice... I'm not sure why, really
                    UsefulValues.currentColour = colourPicker.color
                }
            })
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

        var returnWidget: JComponent? = null
        var widget: JComponent? = null

        var toggleButton: JButton? = null
        var collapsible: JXCollapsiblePane? = null

        loop@ for (annotation in field.annotations) {
            when (annotation) {
                // TODO: Add more argument types
                is IntOpt -> {
                    widget = addInt(annotation, field)
                }
                is DoubleOpt -> {
                    widget = addDouble(annotation, field)
                }
                is IntRangeOpt -> {
                    widget = addIntRange(annotation, field, field.get(null) as IntRange)
                }
                is Colour -> {
                    widget = addColour(annotation, field)
                }
                is Enum -> {
                    widget = addEnum(annotation, field, Class.forName(annotation.enum))
                }
                // These require the widget to exist first, skip them to not add a warning label
                is Toggle -> {
                }
                is Tooltip -> {
                }
                else -> widget = addErrorLabel(annotation)
            }

            // Second loop, for non-component annotations
            widget?.let {
                when (annotation) {
                    is Toggle -> {
                        when (widget) {
                            is JComboBox<*> -> {
                                val frame = JXPanel().apply {
                                    layout = GridBagLayout()
                                    border = BorderFactory.createTitledBorder("${field.name.toCamelCase()} Settings")
                                }

                                collapsible = object : JXCollapsiblePane() {
                                    init {
                                        isCollapsed = true

                                        add(JScrollPane(frame).apply {
                                            border = BorderFactory.createEmptyBorder()
                                        })
                                    }
                                }

                                toggleButton = JButton(collapsible?.actionMap?.get(JXCollapsiblePane.TOGGLE_ACTION)).apply {
                                    text = text.capitalize()
                                }

                                widget.addActionListener {
                                    field.declaringClass.getMethod(annotation.method, Int::class.java, JPanel::class.java).invoke(field.declaringClass.kotlin.objectInstance, widget.selectedIndex, frame)
                                }
                            }
                        }
                    }
                    is Tooltip -> {
                        label?.toolTipText = annotation.string
                        widget.toolTipText = annotation.string
                    }
                }

                if (field.annotations.map { it.annotationClass == Toggle::class }.contains(true)) {
                    if (toggleButton != null && collapsible != null) {
                        if (toggleButton!!.parent == null && collapsible!!.parent == null) {
                            parent.add(JPanel().apply {
                                layout = GridBagLayout()

                                add(widget, ToolOptions.FillHorizontal)
                                add(toggleButton, ToolOptions.FinishLine)
                                add(collapsible, ToolOptions.FillHorizontalFinishLine)
                            }, ToolOptions.FillHorizontalFinishLine)
                        }
                    }
                } else {
                    parent.add(widget, ToolOptions.FillHorizontalFinishLine)
                }
            }

            returnWidget = widget
        }

        return returnWidget
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
                else -> addErrorLabel(annotation)
            }
            parent.add(widget, ToolOptions.FillHorizontalFinishLine)
        }

        return null
    }

    fun addInt(annotation: IntOpt, field: Field): JComponent = Slider.IntSliderComponent(
            annotation.min, annotation.max, field.getInt(null)).apply {
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

    fun addDouble(annotation: DoubleOpt, field: Field): JComponent = Slider.DoubleSliderComponent(
            annotation.min, annotation.max, field.getDouble(null)).apply {
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

    fun addIntRange(annotation: IntRangeOpt, field: Field, range: IntRange): JComponent = RangeSlider.IntRangeSliderComponent(
            annotation.min, annotation.max, range.first, range.last).apply {
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

    fun addColour(annotation: Colour, field: Field): JComponent = JXButton().apply {
        val mattePainter = MattePainter(field.get(null) as Color)
        val compoundPainter = CompoundPainter<JXButton>(CheckerboardPainter(), mattePainter)

        backgroundPainter = compoundPainter

        addActionListener {
            ColorPickerDialog.showDialog(frame, field.get(null) as Color)?.let {
                field.set(null, it)
                mattePainter.fillPaint = it
            }
        }
    }

    fun addEnum(annotation: Enum, field: Field, clazz: Class<*>): JComponent = JComboBox<String>(
            clazz.enumConstants.map { e -> e.toString().toCamelCase() }
                    .toTypedArray()).apply {
        selectedIndex = (clazz.cast(field.get(null)) as kotlin.Enum<*>).ordinal

        addActionListener {
            field.set(null, clazz.enumConstants[this.selectedIndex])

            if (annotation.setter != StringUtils.EMPTY) {
                field.declaringClass.getMethod(annotation.setter).invoke(field.declaringClass.kotlin.objectInstance)
            }
        }
    }

    fun addErrorLabel(annotation: Annotation): JLabel = JLabel("${annotation.annotationClass.qualifiedName} is unsupported!").apply {
        font = font.deriveFont(Font.BOLD)
        foreground = Color.RED
    }
}
