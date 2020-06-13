package com.deflatedpickle.rawky.pluginmanager

import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.BoxLayout

class PluginManagerPanel : JXPanel() {
    class Header : JXPanel() {
        class NameVersion : JXPanel() {
            val nameLabel = JXLabel("name").apply {
                this.font = this.font.deriveFont(26f)
                this.alignmentX = Component.CENTER_ALIGNMENT
                this.alignmentY = Component.CENTER_ALIGNMENT
            }
            val versionLabel = JXLabel("version").apply {
                this.font = this.font.deriveFont(10f)
                this.alignmentX = Component.CENTER_ALIGNMENT
                this.alignmentY = Component.TOP_ALIGNMENT
            }

            init {
                this.layout = BoxLayout(this, BoxLayout.X_AXIS)

                this.add(this.nameLabel)
                this.add(this.versionLabel)
            }
        }

        val nameVersion = NameVersion()
        val authorLabel = JXLabel("author").apply {
            this.font = this.font.deriveFont(14f)
            this.alignmentX = Component.CENTER_ALIGNMENT
            this.alignmentY = Component.CENTER_ALIGNMENT
        }

        class DescriptionPanel : JXPanel() {
            val descriptionLabel = JXLabel("description").apply {
                this.alignmentX = Component.RIGHT_ALIGNMENT
                this.alignmentY = Component.TOP_ALIGNMENT
            }

            init {
                this.layout = BorderLayout()

                this.add(this.descriptionLabel)
            }
        }

        val descriptionPanel = DescriptionPanel()

        init {
            this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

            this.add(this.nameVersion)
            this.add(this.authorLabel)
            this.add(this.descriptionPanel)
        }
    }

    val header = Header()

    init {
        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

        this.add(this.header)
    }
}