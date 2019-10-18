package com.deflatedpickle.rawky.dialogue

import com.deflatedpickle.rawky.api.DoubleRange
import com.deflatedpickle.rawky.api.IntRange
import com.deflatedpickle.rawky.api.Options
import com.deflatedpickle.rawky.api.Tooltip
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.widget.DoubleSlider
import org.reflections.Reflections
import java.awt.*
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class Settings : JDialog(Components.frame, "Settings") {
    companion object {
        val reflections = Reflections("com.deflatedpickle.rawky.component")
    }

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

    // TODO: Replace with an annotation system to define categories and pages
    val node = DefaultMutableTreeNode()

    val tree = JTree(node).apply {
        isRootVisible = false

        expandRow(1)

        addTreeSelectionListener {
            panel.removeAll()

            for (i in reflections.getSubTypesOf(JPanel::class.java)) {
                for (clazz in i.declaredClasses) {
                    if (clazz.annotations.map { it.annotationClass == Options::class }.contains(true)) {
                        for (field in clazz.fields) {
                            if (field.name != "INSTANCE") {
                                val label = JLabel(field.name.capitalize() + ":")
                                panel.add(label, labelConstraints)

                                loop@ for (annotation in field.annotations) {
                                    val widget: JComponent = when (annotation) {
                                        // TODO: Add more argument types
                                        is IntRange -> {
                                            JSlider(annotation.min, annotation.max).apply {
                                                value = field.getInt(null)

                                                addChangeListener {
                                                    field.set(null, value)
                                                }
                                            }
                                        }
                                        is DoubleRange -> {
                                            DoubleSlider(annotation.min, annotation.max, field.getDouble(null), factor = 100.0).apply {
                                                value = (field.getDouble(null) * this.factor).toInt()

                                                addChangeListener {
                                                    field.set(null, value / this.factor)
                                                }
                                            }
                                        }
                                        is Tooltip -> continue@loop
                                        else -> JLabel("${annotation.annotationClass.qualifiedName} is unsupported!").apply {
                                            font = font.deriveFont(Font.BOLD)
                                            foreground = Color.RED
                                        }
                                    }
                                    panel.add(widget, lineEnd)

                                    when (annotation) {
                                        is Tooltip -> {
                                            label.toolTipText = annotation.string
                                            widget.toolTipText = annotation.string
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

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
    }

    fun relayout() {
        this.panel.removeAll()

        for (i in reflections.getSubTypesOf(JPanel::class.java)) {
            for (clazz in i.declaredClasses) {
                if (clazz.annotations.map { it.annotationClass == Options::class }.contains(true)) {
                    with(tree.model as DefaultTreeModel) {
                        with(tree.model.root as DefaultMutableTreeNode) {
                            insertNodeInto(DefaultMutableTreeNode(clazz.simpleName.capitalize()), this, this.childCount)
                        }
                        reload()
                    }
                }
            }
        }

        this.invalidate()
        this.revalidate()
        this.repaint()
    }

    override fun setVisible(b: Boolean) {
        this.relayout()
        super.setVisible(b)
    }
}