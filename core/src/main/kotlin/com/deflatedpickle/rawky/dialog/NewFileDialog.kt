/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:OptIn(InternalSerializationApi::class, InternalSerializationApi::class)

package com.deflatedpickle.rawky.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.template.Template
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.FinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.oxbow.swingbits.dialog.task.TaskDialog
import so.n0weak.ExtendedComboBox
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import javax.swing.*

@OptIn(InternalSerializationApi::class)
class NewFileDialog : TaskDialog(Haruhi.window, "New File") {
    val template = ExtendedComboBox().apply {
        for (i in RawkyPlugin.templateFolder.walk()) {
            if (i.isFile && i.extension == "json") {
                val json = Json.Default.decodeFromString(Template::class.serializer(), i.readText())
                Template.registry[json.name] = json
                addItem(json)
            } else if (i.isDirectory && i.name != "template") {
                addDelimiter(i.name)
            }
        }

        selectedIndex = -1

        addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> {
                    with(this.selectedItem as Template) {
                        columnInput.value = this.width
                        rowInput.value = this.height
                    }
                }
            }
        }
    }

    val columnInput = JSpinner(SpinnerNumberModel(16, 1, null, 8))
    val rowInput = JSpinner(SpinnerNumberModel(16, 1, null, 8))
    private val sizeSwapper = JButton(MonoIcon.SWAP).apply {
        addActionListener {
            val temp = columnInput.value
            columnInput.value = rowInput.value
            rowInput.value = temp
        }
    }

    val framesInput = JSpinner(SpinnerNumberModel(1, 1, null, 1))
    val layersInput = JSpinner(SpinnerNumberModel(1, 1, null, 1))

    val gridModeComboBox = JComboBox<CellProvider<*>>().apply {
        for ((k, v) in CellProvider.registry) {
            addItem(v)
        }
    }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            add(JLabel("Template:"), StickEast)
            add(template, FillHorizontalFinishLine)

            add(JLabel("Size:"), StickEast)
            add(columnInput, FillHorizontal)
            add(JLabel("X"))
            add(rowInput, FillHorizontal)
            add(sizeSwapper, FinishLine)

            add(JSeparator(JSeparator.HORIZONTAL), FillHorizontalFinishLine)

            add(JLabel("Initial Frames:"), StickEast)
            add(framesInput, FillHorizontalFinishLine)
            add(JLabel("Initial Layers:"), StickEast)
            add(layersInput, FillHorizontalFinishLine)

            add(JSeparator(JSeparator.HORIZONTAL), FillHorizontalFinishLine)

            add(JLabel("Grid Mode:"), StickEast)
            add(gridModeComboBox, FillHorizontalFinishLine)
        }
    }
}
