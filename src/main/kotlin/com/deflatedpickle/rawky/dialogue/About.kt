package com.deflatedpickle.rawky.dialogue

import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.widget.HyperLabel
import com.pump.swing.CollapsibleContainer
import org.jdesktop.swingx.JXPanel
import java.awt.*
import java.net.URI
import javax.swing.*

class About : JDialog(Components.frame, "About", true) {
    init {
        layout = BorderLayout()
        size = Dimension(400, 600)

        add(JXPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            add(JLabel("Rawky v0.9.1-alpha").apply {
                font = font.deriveFont(Font.BOLD, 18f)
                alignmentX = Component.CENTER_ALIGNMENT
                horizontalAlignment = SwingConstants.CENTER
            })
            // TODO: Maybe change this to a GitHub logo next to the title?
            add(HyperLabel("GitHub Repository", URI("https://github.com/DeflatedPickle/Rawky")).apply {
                font = font.deriveFont(Font.ITALIC, 12f)
                alignmentX = Component.CENTER_ALIGNMENT
                horizontalAlignment = SwingConstants.CENTER
            })

            add(JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    JScrollPane(
                            JTextArea(
                                    khttp.get("https://raw.githubusercontent.com/DeflatedPickle/Rawky/master/LICENSE").text
                            )
                    ).apply {
                        border = BorderFactory.createTitledBorder("License")
                    },
                    JSplitPane(JSplitPane.VERTICAL_SPLIT,
                            JPanel().apply {
                                border = BorderFactory.createTitledBorder("Libraries")
                                layout = BoxLayout(this, BoxLayout.Y_AXIS)

                                add(JScrollPane(JXPanel().apply {
                                    layout = BoxLayout(this, BoxLayout.Y_AXIS)

                                    scrollableTracksViewportWidth = true
                                    scrollableTracksViewportHeight = false

                                    val collapsibleContainer = CollapsibleContainer()

                                    for ((k, v) in linkedMapOf(
                                            "DockingFrames" to "Benoker/DockingFrames",
                                            "ColorPicker" to "https://mvnrepository.com/artifact/org.drjekyll/colorpicker",
                                            "SwingX" to "https://mvnrepository.com/artifact/org.swinglabs/swingx",
                                            "Pumpernickle" to "mickleness/pumpernickel",
                                            "WrapLayout" to "DeflatedPickle/WrapLayout",
                                            "Dracula" to "bulenkov/Darcula",
                                            "Radiance" to "kirill-grouchnikov/radiance",
                                            "WebLaF" to "mgarin/weblaf",
                                            "GSON" to "google/gson",
                                            "ICAFE" to "dragon66/icafe"
                                    )) {
                                        collapsibleContainer.addSection(k.toLowerCase(), k).apply {
                                            val section = this
                                            collapsibleContainer.getHeader(this).apply {
                                                putClientProperty(CollapsibleContainer.COLLAPSED, true)

                                                layout = BorderLayout()
                                                add(JButton("Website").apply {
                                                    addActionListener {
                                                        Desktop.getDesktop().browse(URI("https://github.com/$v"))
                                                    }
                                                }, BorderLayout.EAST)
                                            }
                                            body.apply {
                                                var first = true
                                                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                                                border = BorderFactory.createTitledBorder("")

                                                collapsibleContainer.getHeader(section).addActionListener {
                                                    if (first) {
                                                        first = false

                                                        // This could be done better, but whatever, right?
                                                        // It's an about window! Who cares about these?
                                                        var request = khttp.get("https://raw.githubusercontent.com/$v/master/LICENSE")
                                                        if (request.statusCode == 404) {
                                                            request = khttp.get("https://raw.githubusercontent.com/$v/master/LICENSE.txt")

                                                            if (request.statusCode == 404) {
                                                                request = khttp.get("https://raw.githubusercontent.com/$v/master/License.txt")
                                                            }
                                                        }

                                                        add(JScrollPane(JTextArea(request.text)))
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    add(collapsibleContainer)
                                }))
                            },
                            JPanel().apply {
                                border = BorderFactory.createTitledBorder("Tools")
                                layout = BoxLayout(this, BoxLayout.Y_AXIS)

                                add(JScrollPane(JXPanel().apply {
                                    layout = BoxLayout(this, BoxLayout.Y_AXIS)

                                    scrollableTracksViewportWidth = true
                                    scrollableTracksViewportHeight = false

                                    for ((k, v) in linkedMapOf(
                                            "Kotlin" to "https://kotlinlang.org/",
                                            "Travis" to "https://travis-ci.org/",
                                            "Gradle" to "https://gradle.org/",
                                            "Shadow" to "https://github.com/johnrengelman/shadow",
                                            "Launch4j" to "http://launch4j.sourceforge.net/"
                                    )) {
                                        add(JButton(k).apply {
                                            addActionListener {
                                                Desktop.getDesktop().browse(URI(v))
                                            }
                                        })
                                    }
                                }))
                            }
                    ).apply {
                        alignmentX = Component.CENTER_ALIGNMENT
                        resizeWeight = 0.5
                    }
            ).apply {
                alignmentX = Component.CENTER_ALIGNMENT
                resizeWeight = 1.0
            })
        })

        add(JPanel().apply {
            add(JButton("OK").apply {
                addActionListener {
                    this@About.dispose()
                }
            })
        }, BorderLayout.PAGE_END)
    }
}