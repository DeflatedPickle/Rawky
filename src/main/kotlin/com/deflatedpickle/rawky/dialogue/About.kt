package com.deflatedpickle.rawky.dialogue

import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.widget.HyperLabel
import java.awt.*
import java.net.URI
import javax.swing.*

class About : JDialog(Components.frame, "About", true) {
    init {
        layout = BorderLayout()
        size = Dimension(400, 400)

        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("Rawky v0.1.0").apply { alignmentX = Component.CENTER_ALIGNMENT })
            add(HyperLabel("GitHub Repository", URI("https://github.com/DeflatedPickle/Rawky")).apply { alignmentX = Component.CENTER_ALIGNMENT })
            add(JPanel().apply {
                border = BorderFactory.createTitledBorder("Credits")
                layout = BoxLayout(this, BoxLayout.Y_AXIS)

                add(HyperLabel("Kotlin", URI("https://kotlinlang.org/")))
                add(HyperLabel("Travis", URI("https://travis-ci.org/")))
                add(HyperLabel("Gradle", URI("https://gradle.org/")))
                add(HyperLabel("Shadow", URI("https://github.com/johnrengelman/shadow")))
                add(HyperLabel("Launch4j", URI("http://launch4j.sourceforge.net/")))
                add(JSeparator())
                add(HyperLabel("DockingFrames", URI("https://github.com/Benoker/DockingFrames")))
                add(HyperLabel("ColorPicker", URI("https://mvnrepository.com/artifact/org.drjekyll/colorpicker")))
                add(HyperLabel("SwingX", URI("https://mvnrepository.com/artifact/org.swinglabs/swingx")))
                add(HyperLabel("WrapLayout", URI("https://github.com/DeflatedPickle/WrapLayout")))
                add(HyperLabel("Dracula", URI("https://github.com/bulenkov/Darcula")))
                add(HyperLabel("Radiance", URI("https://github.com/kirill-grouchnikov/radiance")))
                add(HyperLabel("WebLaF", URI("https://github.com/mgarin/weblaf")))
                add(HyperLabel("GSON", URI("https://github.com/google/gson")))
                add(HyperLabel("ICAFE", URI("https://github.com/dragon66/icafe")))
            }.apply { alignmentX = Component.CENTER_ALIGNMENT })
            add(JScrollPane(JTextArea(khttp.get("https://raw.githubusercontent.com/DeflatedPickle/Rawky/master/LICENSE").text)))
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