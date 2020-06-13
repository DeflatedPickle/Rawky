package com.deflatedpickle.rawky.pluginmanager

import com.deflatedpickle.rawky.constraints.FillHorizontalFinishLine
import com.deflatedpickle.rawky.constraints.StickCenterFinishLine
import com.deflatedpickle.rawky.constraints.StickWestFinishLine
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTextArea
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JTextArea

class PluginManagerPanel : JXPanel() {
    class Header : JXPanel() {
        val nameLabel = JXLabel("name").apply {
            this.font = this.font.deriveFont(26f)
        }
        val versionLabel = JXLabel("version").apply {
            this.font = this.font.deriveFont(10f)
        }

        val authorLabel = JXLabel("author").apply {
            this.font = this.font.deriveFont(14f)
        }

        val descriptionLabel = JXLabel("description")

        init {
            this.layout = GridBagLayout()

            this.add(JXPanel().apply {
                this.layout = GridBagLayout()

                this.add(nameLabel)
                this.add(versionLabel)
            }, StickCenterFinishLine)
            this.add(this.authorLabel, StickCenterFinishLine)
            this.add(this.descriptionLabel, FillHorizontalFinishLine)
        }
    }
    val header = Header()

    class Dependencies : JXPanel() {
        val titleLabel = JXLabel("Dependencies").apply {
            this.font = this.font.deriveFont(18f)
        }

        val dependenciesText = JTextArea().apply {
            this.isEditable = false
        }

        init {
            this.layout = GridBagLayout()

            this.add(this.titleLabel, StickWestFinishLine)
            this.add(this.dependenciesText, FillHorizontalFinishLine)
        }
    }
    val dependencies = Dependencies()

    init {
        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

        this.add(this.header)
        this.add(this.dependencies)
    }
}