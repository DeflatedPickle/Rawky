import java.awt.AlphaComposite
import java.awt.Color
import java.awt.GradientPaint
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.Timer
import javax.swing.WindowConstants

class FLChan(
    jFrame: JFrame,
    var action: FLChanAction,
    private var reflectOpacity: Float,
) {
    private val actions = mutableMapOf<String, List<ImageIcon>>()

    private var frame = -1

    private val label = JLabel()

    init {
        loadSpriteSheet()
        frame = 0

        (jFrame.glassPane as JPanel).add(label)
        jFrame.glassPane.isVisible = true

        Timer(
            1000 / 8
        ) {
            if (frame + 1 < 8) {
                frame++
            } else {
                frame = 0
            }

            label.icon = actions[action.name.lowercase().capitalize()]!![frame]
        }.apply {
            isRepeats = true
            start()
        }

        label.addMouseListener(object : MouseAdapter() {
            var a: FLChanAction? = null
            var me: MouseEvent? = null

            override fun mousePressed(e: MouseEvent) {
                a = action
                action = FLChanAction.HELD
                me = e
            }

            override fun mouseReleased(e: MouseEvent) {
                action = a!!
            }

            override fun mouseDragged(e: MouseEvent) {
                var p = e.point
                p = SwingUtilities.convertPoint(label, p, jFrame.glassPane)
                label.setLocation(p.x - me!!.x, p.y - me!!.y)
            }
        }.also { label.addMouseMotionListener(it) })
    }

    private fun loadSpriteSheet() {
        val sheet = ImageIO.read(FLChanPlugin::class.java.getResource("/Dance_Large.png"))
        val actions = FLChanPlugin::class.java.getResource("/Dance_Large.txt")!!.readText().lines()

        val width = sheet.width / 8
        val height = sheet.height / actions.size

        for ((i, a) in actions.withIndex()) {
            val frames = mutableListOf<ImageIcon>()
            for (f in 0 until 8) {
                val image = sheet.getSubimage(width * f, height * i, width, height)

                frames.add(ImageIcon(
                    BufferedImage(image.width, image.height * 2, BufferedImage.TYPE_INT_ARGB).apply {
                        val g2d = this.createGraphics()

                        g2d.drawImage(image, 0, 0, null)

                        g2d.translate(0.0, height * 2.0)
                        g2d.scale(1.0, -1.0)

                        val reflection = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                        val rg = reflection.createGraphics()
                        rg.drawRenderedImage(image, null)
                        rg.composite = AlphaComposite.DstIn
                        rg.paint = GradientPaint(
                            0f, height * 0.65f, Color(0, 0, 0, 0),
                            0f, height.toFloat(), Color(0f, 0f, 0f, reflectOpacity)
                        )
                        rg.fillRect(0, 0, width, height)
                        rg.dispose()

                        g2d.drawRenderedImage(reflection, null)
                    }
                ))
            }
            this.actions[a] = frames
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val frame = JFrame()
            FLChan(frame, FLChanAction.ZOMBIE, 0.25f)
            frame.setSize(400, 400)
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.isVisible = true
        }
    }
}