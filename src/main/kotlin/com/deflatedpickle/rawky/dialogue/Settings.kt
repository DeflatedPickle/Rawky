/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.dialogue

import com.deflatedpickle.rawky.api.annotations.Options
import com.deflatedpickle.rawky.util.Components
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import org.apache.commons.lang3.StringUtils
import org.reflections.Reflections

object Settings : JDialog(Components.frame, "Settings") {
    val reflections = Reflections("com.deflatedpickle.rawky.component")

    val panel = JPanel()

    val labelConstraints = GridBagConstraints().apply {
        anchor = GridBagConstraints.EAST
    }

    val lineEnd = GridBagConstraints().apply {
        gridwidth = GridBagConstraints.REMAINDER
        weightx = 1.0
        fill = GridBagConstraints.BOTH
    }

    val fill = GridBagConstraints().apply {
        weightx = 1.0
        weighty = 1.0
        fill = GridBagConstraints.BOTH
    }

    val panelList = mutableListOf<JPanel>()

    // TODO: Replace with an annotation system to define categories and pages
    val node = DefaultMutableTreeNode()

    val tree = JTree(node).apply {
        isRootVisible = false

        addTreeSelectionListener {
            panel.removeAll()

            panel.add(panelList[this.minSelectionRow], fill)

            panel.revalidate()
            panel.repaint()
        }
    }

    init {
        layout = BorderLayout()
        size = Dimension(600, 400)

        add(JSplitPane(JSplitPane.HORIZONTAL_SPLIT, JScrollPane(tree), JScrollPane(panel)).apply {
            dividerLocation = 150
        })

        // Gets the options for the window/s and panels
        for (i in reflections.getSubTypesOf(JFrame::class.java) + reflections.getSubTypesOf(JPanel::class.java)) {
            for (clazz in i.declaredClasses) {
                // Checks if a declared class is annotated as Options
                if (clazz.annotations.map { it.annotationClass == Options::class }.contains(true)) {
                    with(tree.model as DefaultTreeModel) {
                        with(tree.model.root as DefaultMutableTreeNode) {
                            insertNodeInto(DefaultMutableTreeNode(StringUtils.splitByCharacterTypeCamelCase(i.simpleName.capitalize()).joinToString(" ")), this, this.childCount)
                        }
                        reload()
                    }

                    val panel = JPanel().apply {
                        layout = GridBagLayout()
                    }

                    // Loops each field and processes the annotations
                    for (field in clazz.fields) {
                        // To make them discoverable, the options class has to be an object
                        // This means we have to find and forget the INSTANCE field
                        if (field.name != "INSTANCE") {
                            Components.processAnnotations(panel, field)
                        }
                    }

                    panelList.add(panel)
                }
            }
        }
    }
}
