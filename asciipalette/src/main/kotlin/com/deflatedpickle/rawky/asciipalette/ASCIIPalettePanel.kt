/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.asciipalette

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.grid.ascii.ASCIICellPlugin
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator
import so.n0weak.ExtendedComboBox
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.GridBagLayout
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.event.ItemEvent
import java.awt.font.GlyphVector
import java.io.File
import java.io.IOException
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane

object ASCIIPalettePanel : PluginPanel() {
    val combo =
        ExtendedComboBox().apply {
            AutoCompleteDecorator.decorate(this)

            addItemListener {
                when (it.stateChange) {
                    ItemEvent.SELECTED -> {
                        panel.removeAll()

                        if (this.selectedItem is Palette<*>) {
                            for (i in (this.selectedItem as Palette<GlyphVector>).items) {
                                panel.add(
                                    JButton(ASCIIIcon(i.key)).apply {
                                        toolTipText = i.value

                                        addActionListener {
                                            ASCIICellPlugin.current = (icon as ASCIIIcon).glyph
                                        }

                                        EventChangeColour.addListener {
                                            repaint()
                                        }
                                    },
                                )
                            }
                        }

                        panel.revalidate()
                        panel.repaint()
                    }
                }
            }
        }

    private val panel = JPanel().apply { layout = WrapLayout() }

    private val dropTargetAdapter = object : DropTargetAdapter() {
        override fun dragOver(dtde: DropTargetDragEvent) {
            try {
                if (dtde.transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    val fileList = (dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>)

                    // TODO: could accept multiple files?
                    if (fileList.size > 1) dtde.rejectDrag()

                    val file = fileList.first()

                    // TODO: could support folders?
                    if (!file.isFile) dtde.rejectDrag()

                    if (file.extension in ASCIIPalettePlugin.registry.keys) {
                        dtde.acceptDrag(DnDConstants.ACTION_COPY)
                    }
                }
            } catch (_: IOException) {
            }
        }

        override fun drop(dtde: DropTargetDropEvent) {
            when {
                dtde.transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor) -> {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY)

                    val transferable = (dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>)
                        .first()

                    ASCIIPalettePlugin.registry[transferable.extension]?.let { pp ->
                        val pallet = pp.parse(transferable)
                        combo.addItem(pallet)
                        combo.selectedItem = pallet
                    }

                    dtde.dropComplete(true)
                }
            }

            dtde.rejectDrop()
        }
    }

    init {
        layout = GridBagLayout()

        DropTarget(panel, DnDConstants.ACTION_COPY, dropTargetAdapter, true)

        add(combo, FillHorizontalFinishLine)
        add(JScrollPane(panel), FillBothFinishLine)

        EventProgramFinishSetup.addListener {
            for (i in ASCIIPalettePlugin.folder.walk()) {
                if (i.isFile) {
                    ASCIIPalettePlugin.registry[i.extension]?.let { pp -> combo.addItem(pp.parse(i)) }
                }
            }
        }
    }
}
