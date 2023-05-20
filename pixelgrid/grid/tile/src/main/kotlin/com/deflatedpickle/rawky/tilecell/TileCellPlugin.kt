@file:Suppress("unused")

package com.deflatedpickle.rawky.tilecell

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.marvin.extensions.getSubimage
import com.deflatedpickle.marvin.serializer.BufferedImageSerializer
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.event.EventRegisterCellClass
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.tilecell.collection.TileCell
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.subclass
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Plugin(
    value = "tile_cell",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a cell that holds an image
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@pixel_grid#*",
    ],
)
object TileCellPlugin : CellProvider<BufferedImage>() {
    override val name = "Tile"
    override var current: BufferedImage = TileCell.default

    init {
        registry[name] = this
    }

    override fun provide(
        row: Int, column: Int,
    ) = TileCell(row, column, TileCell.default)

    override fun perform(
        cell: Cell<Any>, button: Int,
        dragged: Boolean, clickCount: Int
    ) {
        when (button) {
            0 -> cell.content = current
        }
    }

    override fun redact(
        cell: Cell<Any>, button: Int,
        dragged: Boolean, clickCount: Int
    ) {
    }

    override fun cleanup(
        cache: Any,
        cell: Cell<Any>,
        button: Int,
        dragged: Boolean,
        clickCount: Int
    ) {
    }

    override fun paintGrid(
        g: Graphics2D,
        cell: Cell<@Contextual Any>
    ) {
        g.drawImage(
            (cell as TileCell).content,
            cell.polygon.x, cell.polygon.y,
            null
        )
    }

    override fun paintHover(
        g: Graphics2D,
        cell: Cell<@Contextual Any>
    ) {
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)
        g.drawImage(
            (cell as TileCell).content,
            cell.polygon.x, cell.polygon.y,
            null
        )
    }
}