package com.deflatedpickle.rawky.dialogue

import com.deflatedpickle.rawky.utils.Components
import org.pushingpixels.substance.api.skin.SubstanceGraphiteElectricLookAndFeel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode

class Settings : JDialog(Components.frame, "Settings") {
    val panel = JPanel().apply {
        layout = GridBagLayout()
    }

    val appearanceComboBoxModel = DefaultComboBoxModel(arrayOf("Metal", "System", "Substance Graphite Electric"))

    init {
        layout = GridBagLayout()
        size = Dimension(600, 400)

        val node = DefaultMutableTreeNode().apply {
            add(DefaultMutableTreeNode("Appearance"))
        }

        add(JScrollPane(JTree(node).apply {
            addTreeSelectionListener {
                panel.removeAll()

                when (it.path.lastPathComponent.toString()) {
                    "Appearance" -> {
                        panel.add(JLabel("Look and Feel:"), GridBagConstraints().apply {
                            weightx = 1.0
                            anchor = GridBagConstraints.EAST
                        })
                        panel.add(JComboBox<String>(appearanceComboBoxModel).apply {
                            addActionListener {
                                when (selectedIndex) {
                                    0 -> UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
                                    1 -> UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
                                    2 -> UIManager.setLookAndFeel(SubstanceGraphiteElectricLookAndFeel())
                                }
                                SwingUtilities.updateComponentTreeUI(Components.frame)
                                SwingUtilities.updateComponentTreeUI(this@Settings)
                            }
                        })
                    }
                }
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