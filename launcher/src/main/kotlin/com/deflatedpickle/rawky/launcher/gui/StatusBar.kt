/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.launcher.gui

import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.ControlMode
import com.deflatedpickle.rawky.event.EventChangeFrame
import com.deflatedpickle.rawky.event.EventChangeLayer
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.pixelgrid.event.EventReadProgress
import com.deflatedpickle.rawky.pixelgrid.event.EventWriteProgress
import org.jdesktop.swingx.JXStatusBar
import java.awt.Dimension
import java.awt.event.ItemEvent
import javax.swing.AbstractButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPopupMenu
import javax.swing.JProgressBar
import javax.swing.MenuElement
import javax.swing.SwingUtilities
import kotlin.math.max
import kotlin.math.roundToInt

object StatusBar : JXStatusBar() {
    var note = " "
        set(value) {
            field = value
            noteLabel.text = field

            revalidate()
            repaint()
        }

    private val noteLabel = JLabel(note)

    private val mouseGridPositionLabel = JLabel()

    private val sizeLabel = JLabel()
    private val frameLabel = JLabel()
    private val layerLabel = JLabel()

    private val colourSpaceLabel = JLabel()
    private val cellProviderLabel = JLabel()
    private val programProcessBar = JProgressBar(0, 100).apply {
        isStringPainted = true
    }
    private val controlModeLabel = JComboBox<ControlMode>().apply {
        isEnabled = false

        EventProgramFinishSetup.addListener {
            for ((_, v) in ControlMode.registry) {
                addItem(v)
            }

            selectedItem = ControlMode.default

            addItemListener {
                when (it.stateChange) {
                    ItemEvent.SELECTED -> {
                        ControlMode.current = it.item as ControlMode
                        RawkyPlugin.document?.controlMode = ControlMode.current
                        ControlMode.current.apply()
                    }
                    ItemEvent.DESELECTED -> (it.item as ControlMode).remove()
                }
            }
        }
    }

    init {
        isResizeHandleEnabled = true

        add(noteLabel, Constraint(Constraint.ResizeBehavior.FILL))

        EventProgramFinishSetup.addListener {
            setStaticLabels()
            setDynamicLabels()
        }

        EventCreateDocument.addListener {
            addLabels()
            setStaticLabels()
            setDynamicLabels()

            programProcessBar.value = 0
            programProcessBar.toolTipText = ""

            controlModeLabel.isEnabled = true
        }

        EventOpenDocument.addListener {
            addLabels()
            setStaticLabels()
            setDynamicLabels()

            programProcessBar.value = 0
            programProcessBar.toolTipText = ""

            controlModeLabel.isEnabled = true
        }

        EventChangeFrame.addListener {
            setDynamicLabels()
        }

        EventChangeLayer.addListener {
            setDynamicLabels()
        }

        EventUpdateCell.addListener {
            mouseGridPositionLabel.text =
                "${it.row}x${it.column}"
        }

        EventReadProgress.addListener {
            programProcessBar.value = it.progress.roundToInt()

            if (it.progress.roundToInt() >= 100) {
                programProcessBar.toolTipText = "Finished Reading \"${it.file.absolutePath}\""
            } else {
                programProcessBar.toolTipText = "Reading \"${it.file.absolutePath}\"..."
            }
        }

        EventWriteProgress.addListener {
            programProcessBar.value = it.progress.roundToInt()

            if (it.progress.roundToInt() >= 100) {
                programProcessBar.toolTipText = "Finished Writing \"${it.file.absolutePath}\""
            } else {
                programProcessBar.toolTipText = "Writing \"${it.file.absolutePath}\"..."
            }
        }

        SwingUtilities.invokeLater {
            for (m in 0 until Window.jMenuBar.menuCount) {
                Window.jMenuBar.getMenu(m)?.let { menu ->
                    recurseMenu(menu)
                }
            }
        }
    }

    private fun addLabels() {
        add(
            mouseGridPositionLabel.apply {
                RawkyPlugin.document?.let { doc ->
                    val m = max(doc.rows, doc.columns)
                    preferredSize = Dimension(
                        getFontMetrics(font).stringWidth("${m}x$m "),
                        preferredSize.height,
                    )
                }
            },
            0,
        )
        add(sizeLabel)
        add(frameLabel)
        add(layerLabel)
        add(colourSpaceLabel)
        add(cellProviderLabel)
        add(programProcessBar)
        add(controlModeLabel)
    }

    private fun setStaticLabels() {
        RawkyPlugin.document?.let {
            sizeLabel.text = "${it.rows}x${it.columns}"
            colourSpaceLabel.text = "Colour Space: ${it.colourChannel.name}"
            cellProviderLabel.text = "Cell Mode: ${CellProvider.current.name}"
        }
    }

    private fun setDynamicLabels() {
        RawkyPlugin.document?.let { doc ->
            mouseGridPositionLabel.text = "0x0"

            val frame = doc.children[doc.selectedIndex]
            val frameIndex = doc.selectedIndex + 1
            val layerIndex = frame.selectedIndex + 1

            frameLabel.text = "Frame $frameIndex/${doc.children.size}"
            layerLabel.text = "Layer $layerIndex/${frame.children.size}"
        }
    }

    private fun recurseMenu(item: MenuElement) {
        if (item is AbstractButton) {
            item.addChangeListener {
                note = (it.source as AbstractButton).getClientProperty("statusMessage") as String? ?: " "

                for (i in item.subElements) {
                    recurseMenu(i)
                }
            }
        } else if (item is JPopupMenu) {
            for (i in item.subElements) {
                recurseMenu(i)
            }
        }
    }
}
