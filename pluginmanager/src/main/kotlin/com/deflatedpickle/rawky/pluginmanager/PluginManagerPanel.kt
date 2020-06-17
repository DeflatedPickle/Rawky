package com.deflatedpickle.rawky.pluginmanager

import com.deflatedpickle.rawky.ui.constraints.FillHorizontalFinishLine
import com.deflatedpickle.rawky.ui.constraints.StickCenterFinishLine
import com.deflatedpickle.rawky.ui.constraints.StickWestFinishLine
import org.jdesktop.swingx.*
import java.awt.*
import javax.swing.BoxLayout

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

        val typeLabel = JXLabel("type").apply {
            this.font = this.font.deriveFont(10f)
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
            this.add(this.typeLabel, StickWestFinishLine)
            this.add(this.descriptionLabel, FillHorizontalFinishLine)
        }
    }
    val header = Header()

    class Dependencies : JXPanel() {
        val titleLabel = JXLabel("Dependencies").apply {
            this.font = this.font.deriveFont(18f)
        }

        val dependenciesTableTree = JXTreeTable()

        init {
            this.layout = GridBagLayout()

            this.add(this.titleLabel, StickWestFinishLine)
            this.add(this.dependenciesTableTree, FillHorizontalFinishLine)
        }
    }
    val dependencies = Dependencies()

    init {
        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

        this.add(this.header)
        this.add(this.dependencies)
    }
}