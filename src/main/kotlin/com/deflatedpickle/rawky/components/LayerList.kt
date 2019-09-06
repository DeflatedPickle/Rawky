package com.deflatedpickle.rawky.components

import com.deflatedpickle.rawky.Icons
import java.awt.BorderLayout
import javax.swing.*

class LayerList : JPanel() {
    val listModel = DefaultListModel<String>()
    val list = JList(listModel)

    init {
        isOpaque = false
        layout = BorderLayout()

        add(list)

        add(JToolBar().apply {
            add(JButton(Icons.plus).apply {
                addActionListener {
                    addLayer()
                }
            })
        }, BorderLayout.PAGE_END)
    }

    fun addLayer() {
        listModel.add(0, "Layer ${listModel.size()}")
        list.selectedIndex = 0

        Components.pixelGrid.layerList.add(0, PixelGrid.Layer())
    }
}