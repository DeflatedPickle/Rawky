/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.transfer

import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.util.Components
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.DnDConstants
import java.util.Collections
import javax.swing.DefaultListModel
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.JTable
import javax.swing.TransferHandler
import javax.swing.table.DefaultTableModel

class RowTransfer(val index: Int) : Transferable {
    companion object {
        val dataFlavor = DataFlavor(Int::class.java, this::class.java.canonicalName)
    }

    override fun getTransferData(flavor: DataFlavor?): Any = index
    override fun isDataFlavorSupported(flavor: DataFlavor?): Boolean = flavor == dataFlavor
    override fun getTransferDataFlavors(): Array<DataFlavor> = arrayOf(dataFlavor)

    class ExportImport(val component: JComponent) : TransferHandler() {
        override fun getSourceActions(c: JComponent?): Int = DnDConstants.ACTION_COPY_OR_MOVE
        override fun createTransferable(c: JComponent?): Transferable? {
            return if (component is JTable) {
                RowTransfer(component.selectedRow)
            } else if (component is JList<*>) {
                RowTransfer(component.selectedIndex)
            } else {
                null
            }
        }

        override fun canImport(support: TransferSupport?): Boolean = support?.isDataFlavorSupported(dataFlavor) ?: false
        override fun importData(support: TransferSupport?): Boolean {
            var accept = false

            support?.let {
                if (canImport(support)) {
                    with(support.transferable.getTransferData(dataFlavor) as Int) {
                        var index = when (component) {
                            is JTable -> (support.dropLocation as JTable.DropLocation).row
                            is JList<*> -> (support.dropLocation as JList.DropLocation).index
                            else -> return false
                        }

                        if (this != -1 && this != index) {

                            when (component) {
                                is JTable -> {
                                    (component.model as DefaultTableModel).moveRow(this, this, index)
                                    Collections.swap(PixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList, this, index)
                                }
                                is JList<*> -> {
                                    val model = (component.model as DefaultListModel<String>)

                                    val firstItem = model.getElementAt(this)
                                    val secondItem = model.getElementAt(index)

                                    model.set(this, secondItem)
                                    model.set(index, firstItem)

                                    Collections.swap(PixelGrid.frameList, this, index)
                                }
                            }

                            PixelGrid.repaintWithChildren()

                            if (index > this) index--

                            when (component) {
                                is JTable -> component.selectionModel.addSelectionInterval(index, index)
                                is JList<*> -> component.selectionModel.addSelectionInterval(index, index)
                            }

                            accept = true
                        }
                    }
                }
            }

            return accept
        }
    }
}
