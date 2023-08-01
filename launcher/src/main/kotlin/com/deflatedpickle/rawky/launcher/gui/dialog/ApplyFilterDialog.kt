/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.launcher.gui.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.marvin.extensions.get
import com.deflatedpickle.marvin.extensions.set
import com.deflatedpickle.rawky.api.FilterCollection
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import com.deflatedpickle.undulation.widget.ColourSelectButton
import com.deflatedpickle.undulation.widget.SliderSpinner
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Color
import java.awt.GridBagLayout
import java.awt.Point
import java.awt.geom.Point2D
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

class ApplyFilterDialog(
    private val packet: FilterCollection.ArgumentFilter.Packet,
) : TaskDialog(Haruhi.window, "Apply Filter") {
    init {
        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            // TODO: add a live preview

            for (p in packet::class.memberProperties) {
                add(JLabel(p.name), StickEast)
                add(propertyToWidget(p), FillHorizontalFinishLine)
            }
        }

        setCommands(StandardCommand.OK, StandardCommand.CANCEL)
    }

    private fun propertyToWidget(p: KProperty<*>): JComponent =
        when (p.returnType) {
            Int::class.createType() -> intWidget(p)
            Float::class.createType() -> floatWidget(p)
            Boolean::class.createType() -> booleanWidget(p)
            Point::class.createType() -> pointWidget(p)
            Point2D.Float::class.createType() -> point2DWidget(p)
            Color::class.createType() -> colourWidget(p)
            else -> JLabel()
        }

    private fun intWidget(p: KProperty<*>) =
        SliderSpinner(packet.get(p.name), 0, 100)
            .apply {
                addChangeListener {
                    when (val src = it.source) {
                        is JSpinner -> packet.set(p.name, src.value)
                    }
                }
            }

    private fun floatWidget(p: KProperty<*>) =
        SliderSpinner(packet.get(p.name), 0f, 100f)
            .apply {
                addChangeListener {
                    when (val src = it.source) {
                        // for some reason the value of the slider is always 0
                        is JSpinner -> packet.set(p.name, src.value)
                    }
                }
            }

    private fun booleanWidget(p: KProperty<*>) =
        JCheckBox(null, null, packet.get(p.name))
            .apply {
                addChangeListener {
                    packet.set(
                        p.name,
                        (it.source as JCheckBox).isSelected,
                    )
                }
            }

    // TODO: use intWidget
    private fun pointWidget(p: KProperty<*>) =
        JPanel().apply {
            add(
                SliderSpinner(packet.get<Point>(p.name).x, 0, 100)
                    .apply {
                        addChangeListener {
                            when (val src = it.source) {
                                is JSpinner -> packet.get<Point>(p.name).setLocation(
                                    src.value as Int,
                                    packet.get<Point>(p.name).y,
                                )
                            }
                        }
                    },
            )

            add(
                SliderSpinner(packet.get<Point>(p.name).y, 0, 100)
                    .apply {
                        addChangeListener {
                            when (val src = it.source) {
                                is JSpinner -> packet.get<Point>(p.name).setLocation(
                                    packet.get<Point>(p.name).x,
                                    src.value as Int,
                                )
                            }
                        }
                    },
            )
        }

    // TODO: use floatWidget
    private fun point2DWidget(p: KProperty<*>) =
        JPanel().apply {
            add(
                SliderSpinner(packet.get<Point2D>(p.name).x, 0.0, 100.0)
                    .apply {
                        addChangeListener {
                            when (val src = it.source) {
                                is JSpinner -> packet.get<Point2D>(p.name).setLocation(
                                    src.value as Double,
                                    packet.get<Point2D>(p.name).y,
                                )
                            }
                        }
                    },
            )

            add(
                SliderSpinner(packet.get<Point2D>(p.name).y, 0.0, 100.0)
                    .apply {
                        addChangeListener {
                            when (val src = it.source) {
                                is JSpinner -> packet.get<Point2D>(p.name).setLocation(
                                    packet.get<Point2D>(p.name).x,
                                    src.value as Double,
                                )
                            }
                        }
                    },
            )
        }

    private fun colourWidget(p: KProperty<*>) =
        ColourSelectButton(packet.get(p.name)).apply {
            addChangeListener {
                packet.set(
                    p.name,
                    (it.source as ColourSelectButton).colour,
                )
            }
        }
}
