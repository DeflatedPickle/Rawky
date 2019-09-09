package com.deflatedpickle.rawky.dialogue

import com.alee.laf.WebLookAndFeel
import com.bric.colorpicker.ColorPickerDialog
import com.bulenkov.darcula.DarculaLaf
import com.deflatedpickle.rawky.utils.Components
import org.pushingpixels.substance.api.skin.SubstanceGraphiteElectricLookAndFeel
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode

class Settings : JDialog(Components.frame, "Settings") {
    val panel = JPanel().apply {
        layout = GridBagLayout()
    }

    val labelConstraints = GridBagConstraints().apply {
        weightx = 1.0
        anchor = GridBagConstraints.EAST
    }

    val lineEnd = GridBagConstraints().apply {
        gridwidth = GridBagConstraints.REMAINDER
    }

    val appearanceComboBoxModel = DefaultComboBoxModel(arrayOf("Metal", "System", "Dracula", "Substance Graphite Electric", "Web"))

    init {
        layout = GridBagLayout()
        size = Dimension(600, 400)

        val node = DefaultMutableTreeNode().apply {
            add(DefaultMutableTreeNode("Appearance"))
            add(DefaultMutableTreeNode("Components").apply {
                add(DefaultMutableTreeNode("Colour Library"))
                add(DefaultMutableTreeNode("Colour Palette"))
                add(DefaultMutableTreeNode("Colour Shades"))
                add(DefaultMutableTreeNode("Layer List"))
                add(DefaultMutableTreeNode("Pixel Grid"))
                add(DefaultMutableTreeNode("Tiled View"))
                add(DefaultMutableTreeNode("Toolbox"))
            })
        }

        add(JScrollPane(JTree(node).apply {
            isRootVisible = false
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
                        })
                    }
                    ">components>pixel_grid" -> {
                        panel.add(JLabel("Pixel Smooth:"), labelConstraints)
                        panel.add(JSlider(0, 200, Components.pixelGrid.pixelSmooth).apply { addChangeListener { Components.pixelGrid.pixelSmooth = this.value } }, lineEnd)

                        panel.add(JLabel("Background Fill Even:"), labelConstraints)
                        panel.add(JButton().apply { addActionListener { Components.pixelGrid.backgroundFillEven = ColorPickerDialog.showDialog(this@Settings, Components.pixelGrid.backgroundFillEven) } }, lineEnd)

                        panel.add(JLabel("Background Fill Odd:"), labelConstraints)
                        panel.add(JButton().apply { addActionListener { Components.pixelGrid.backgroundFillOdd = ColorPickerDialog.showDialog(this@Settings, Components.pixelGrid.backgroundFillOdd) } }, lineEnd)

                        panel.add(JLabel("Hover Opacity:"), labelConstraints)
                        panel.add(JSlider(1, 255, Components.pixelGrid.hoverOpacity).apply { addChangeListener { Components.pixelGrid.hoverOpacity = this.value } }, lineEnd)

                        panel.add(JLabel("Line Thickness:"), labelConstraints)
                        panel.add(JSlider(1, 200, Components.pixelGrid.lineThickness.toInt()).apply { addChangeListener { Components.pixelGrid.lineThickness = (this.value / 10).toFloat() } }, lineEnd)

                        panel.add(JLabel("Grid Colour:"), labelConstraints)
                        panel.add(JButton().apply { addActionListener { Components.pixelGrid.gridColour = ColorPickerDialog.showDialog(this@Settings, Components.pixelGrid.gridColour) } }, lineEnd)
                    }
                }
                panel.revalidate()
            }
        }), GridBagConstraints().apply {
            weightx = 0.0
            weighty = 1.0
            fill = GridBagConstraints.VERTICAL
        })
        add(JScrollPane(panel), GridBagConstraints().apply {
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
        })
    }
}