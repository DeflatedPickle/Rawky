package com.deflatedpickle.rawky.launcher.gui

import com.deflatedpickle.marvin.functions.extensions.size
import org.apache.commons.io.FileUtils
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.FileImageInputStream
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JFileChooser
import kotlin.math.max

// TODO: make content scrollable
// TODO: add previews for more file types
class FilePreview(fc: JFileChooser) : JComponent() {
    private var thumbnail: ImageIcon? = null
    private var reader: ImageReader? = null
    private var image: BufferedImage? = null

    var file: File? = null

    init {
        fc.addPropertyChangeListener {
            var update = false

            when (it.propertyName) {
                JFileChooser.DIRECTORY_CHANGED_PROPERTY -> {
                    file = null
                    update = true
                }

                JFileChooser.SELECTED_FILES_CHANGED_PROPERTY -> {
                    // TODO: show multiple files in a stack
                }

                JFileChooser.SELECTED_FILE_CHANGED_PROPERTY -> {
                    if (it.newValue == null) {
                        preferredSize = Dimension(0, 0)
                        revalidate()
                    } else {
                        preferredSize = Dimension(100, 50)
                        revalidate()
                    }

                    file = it.newValue as File?
                    update = true
                }
            }

            if (update) {
                thumbnail = null
                if (isShowing) {
                    loadImage()
                    repaint()
                }
            }
        }

        fc.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                if (isShowing) {
                    loadImage()
                    repaint()
                }
            }
        })
    }

    private fun loadImage() {
        if (file == null) {
            thumbnail = null
            reader = null
            image = null
            return
        }

        file?.let { file ->
            val readers = ImageIO.getImageReadersByFormatName(file.extension)

            if (readers.hasNext()) {
                reader = readers.next()
                reader!!.input = FileImageInputStream(file)
                image = reader!!.read(0)
                thumbnail = ImageIcon(image!!.getScaledInstance(96, 96, Image.SCALE_FAST))
            }
        }
    }

    override fun paintComponent(g: Graphics) {
        if (thumbnail == null) {
            loadImage()
        }

        val g2D = g as Graphics2D

        thumbnail?.let { thumbnail ->
            reader?.let { reader ->
                image?.let { image ->
                    val metrics = getMetrics(reader, image)

                    g2D.translate(
                        max(0, width / 2 - thumbnail.iconWidth / 2 + 8),
                        max(0, height / 2 - thumbnail.iconHeight / 2)
                    )

                    if (height / 2 + thumbnail.iconHeight / 2 + metrics.size * g.fontMetrics.height + 2 > height) {
                        g2D.translate(
                            0, -2 * g.fontMetrics.height
                        )
                    }

                    for (r in 0 until thumbnail.iconHeight / 16) {
                        for (c in 0 until thumbnail.iconWidth / 16) {
                            g.color = if (r % 2 == c % 2) Color.LIGHT_GRAY else Color.WHITE
                            g.fillRect(c * 16, r * 16, 16, 16)
                        }
                    }

                    g.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC
                    )

                    thumbnail.paintIcon(this, g, 0, 0)

                    g.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
                    )
                    g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON,
                    )
                    g.setRenderingHint(
                        RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY,
                    )

                    g.translate(thumbnail.iconWidth / 2, thumbnail.iconHeight + g.fontMetrics.height + 4)

                    g.font = g.font.deriveFont(12f)

                    for (i in metrics) {
                        g.drawString(i, x - g.fontMetrics.stringWidth(i) / 2, 0)
                        g.translate(0, g.fontMetrics.height + 2)
                    }
                }
            }
        }
    }

    private fun getMetrics(reader: ImageReader, image: BufferedImage): List<String> {
        val metrics = mutableListOf<String>()

        val colourSpace = when {
            image.colorModel.colorSpace.isCS_sRGB -> "sRGB"
            else -> ""
        }
        val transparency = when (image.colorModel.transparency) {
            Transparency.TRANSLUCENT -> "alpha"
            else -> ""
        }

        metrics.add(FileUtils.byteCountToDisplaySize(file!!.size()))
        metrics.add("${image.width}x${image.height}")

        if (colourSpace.isNotBlank()) {
            var str = colourSpace

            if (transparency.isNotBlank()) {
                str += "-$transparency"
            }

            metrics.add(str)
        }

        metrics.add("${reader.getNumImages(true)} Images")

        if (reader.readerSupportsThumbnails()) {
            metrics.add("${reader.getNumThumbnails(0)} Thumbnails")
        }

        return metrics
    }
}