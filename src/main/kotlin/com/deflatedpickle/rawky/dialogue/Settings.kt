package com.deflatedpickle.rawky.dialogue

import com.alee.laf.WebLookAndFeel
import com.bric.colorpicker.ColorPickerDialog
import com.bulenkov.darcula.DarculaLaf
import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.util.Components
import org.pushingpixels.substance.api.skin.SubstanceGraphiteElectricLookAndFeel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

class Settings : JDialog(Components.frame, "Settings") {
    val panel = JPanel().apply {
        layout = GridBagLayout()
    }

    val labelConstraints = GridBagConstraints().apply {
        anchor = GridBagConstraints.EAST
    }

    val lineEnd = GridBagConstraints().apply {
        gridwidth = GridBagConstraints.REMAINDER
        weightx = 1.0
        fill = GridBagConstraints.BOTH
    }

    // TODO: Add an annotation system to define categories and pages
    val node = DefaultMutableTreeNode().apply {
        add(DefaultMutableTreeNode("Appearance"))
        add(DefaultMutableTreeNode("Components").apply {
            // TODO: Add more settings
            // add(DefaultMutableTreeNode("Colour Library"))
            // add(DefaultMutableTreeNode("Colour Palette"))
            // add(DefaultMutableTreeNode("Colour Shades"))
            // add(DefaultMutableTreeNode("Layer List"))
            add(DefaultMutableTreeNode("Pixel Grid"))
            // add(DefaultMutableTreeNode("Tiled View"))
            // add(DefaultMutableTreeNode("Toolbox"))
        })
    }

    val tree = JTree(node).apply {
        isRootVisible = false

        expandRow(1)

        addTreeSelectionListener {
            panel.removeAll()

            val pathTemp = mutableListOf<String>()
            for (i in 0 until it.path.pathCount) {
                pathTemp.add(it.path.getPathComponent(i).toString().replace(" ", "_"))
            }

            when (pathTemp.joinToString(">").toLowerCase()) {
                ">appearance" -> {
                    panel.add(JLabel("Look and Feel:"), labelConstraints)
                    panel.add(JComboBox<String>(appearanceComboBoxModel).apply {
                        selectedIndex = Components.frame.theme

                        addActionListener {
                            when (selectedIndex) {
                                0 -> UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
                                1 -> UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
                                2 -> UIManager.setLookAndFeel(DarculaLaf())
                                3 -> UIManager.setLookAndFeel(SubstanceGraphiteElectricLookAndFeel())
                                4 -> UIManager.setLookAndFeel(WebLookAndFeel())
                            }
                            SwingUtilities.updateComponentTreeUI(Components.frame)
                            SwingUtilities.updateComponentTreeUI(this@Settings)
                        }
                    }, lineEnd)
                }
                ">components>pixel_grid" -> {
                    panel.add(JLabel("Pixel Smooth:"), labelConstraints)
                    panel.add(JSlider(0, 200, Components.pixelGrid.pixelSmooth).apply { addChangeListener { Components.pixelGrid.pixelSmooth = this.value } }, lineEnd)

                    panel.add(JLabel("Hover Opacity:"), labelConstraints)
                    panel.add(JSlider(1, 255, Components.pixelGrid.hoverOpacity).apply { addChangeListener { Components.pixelGrid.hoverOpacity = this.value } }, lineEnd)

                    panel.add(JLabel("Line Thickness:"), labelConstraints)
                    panel.add(JSlider(1, 200, Components.pixelGrid.lineThickness.toInt()).apply { addChangeListener { Components.pixelGrid.lineThickness = (this.value / 10).toFloat() } }, lineEnd)

                    panel.add(JLabel("Grid Colour:"), labelConstraints)
                    panel.add(JButton().apply { addActionListener { Components.pixelGrid.gridColour = ColorPickerDialog.showDialog(this@Settings, Components.pixelGrid.gridColour) } }, lineEnd)

                    panel.add(JPanel().apply {
                        border = BorderFactory.createTitledBorder("Background")
                        layout = GridBagLayout()

                        add(JLabel("Pixel Size:"), labelConstraints)
                        add(JSlider(Components.pixelGrid.pixelSize / 3, 100, Components.pixelGrid.backgroundPixelSize).apply { addChangeListener { Components.pixelGrid.backgroundPixelSize = this.value } }, lineEnd)

                        add(JLabel("Fill Type:"), labelConstraints)
                        add(JComboBox<String>(PixelGrid.FillType.values().map { i -> i.name.toLowerCase().capitalize() }.toTypedArray()).apply {
                            selectedIndex = Components.pixelGrid.backgroundFillType.ordinal

                            addActionListener {
                                Components.pixelGrid.backgroundFillType = PixelGrid.FillType.values()[this.selectedIndex]
                            }
                        }, lineEnd)

                        add(JLabel("Colour Even:"), labelConstraints)
                        add(JButton().apply { addActionListener { Components.pixelGrid.backgroundFillEven = ColorPickerDialog.showDialog(this@Settings, Components.pixelGrid.backgroundFillEven) } }, lineEnd)

                        add(JLabel("Colour Odd:"), labelConstraints)
                        add(JButton().apply { addActionListener { Components.pixelGrid.backgroundFillOdd = ColorPickerDialog.showDialog(this@Settings, Components.pixelGrid.backgroundFillOdd) } }, lineEnd)
                    }, lineEnd)
                }
            }
            panel.revalidate()
            panel.repaint()
        }
    }

    val appearanceComboBoxModel = DefaultComboBoxModel(arrayOf("Metal", "System", "Dracula", "Substance Graphite Electric", "Web"))

    init {
        layout = BorderLayout()
        size = Dimension(600, 400)

        add(JSplitPane(JSplitPane.HORIZONTAL_SPLIT, JScrollPane(tree), JScrollPane(panel)).apply {
            dividerLocation = 150
        })
    }
}