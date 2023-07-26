/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.redraw.RedrawActive
import com.deflatedpickle.haruhi.api.registry.Registry
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.event.EventImportDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.api.impex.Importer
import com.deflatedpickle.rawky.api.impex.Opener
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.util.DnDUtil
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.swing.JPanel
import javax.swing.JToolBar
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

// TODO: add d&d of images
@RedrawActive
object PixelGridPanel : PluginPanel() {
    val selectedCells = mutableListOf<Cell<Any>>()

    private val quickBar = JToolBar().apply {
        EventChangeTool.addListener { tool ->
            removeAll()

            // FIXME: changes are delayed for some reason
            for (i in tool.getQuickSettings()) {
                (RegistryUtil.get("setting_type") as Registry<String, (Plugin, String, Any) -> Component>?)
                    ?.let { registry ->
                        val settings = Tool.current.getSettings()!!
                        val prop = settings::class.declaredMemberProperties.first { it.name == i }

                        for (
                        t in
                        mutableListOf(
                            prop.returnType,
                            *(prop.returnType.classifier as KClass<*>)
                                .supertypes
                                .toTypedArray()
                        )
                        ) {
                            val clazz = (t.classifier as KClass<*>)
                            val clazzName = clazz.qualifiedName!!
                            registry.get(clazzName)?.let {
                                val widget = it(plugin, prop.name, settings)
                                add(widget)
                            }
                        }
                    }
            }
        }
    }
    val panel = object : JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            RawkyPlugin.document?.let { doc ->
                doc.children.getOrNull(doc.selectedIndex)?.let { frame ->
                    val g2d = g as Graphics2D
                    val bufferedImage =
                        BufferedImage(
                            visibleRect.x + visibleRect.width,
                            visibleRect.y + visibleRect.height,
                            BufferedImage.TYPE_INT_ARGB,
                        )

                    for (v in PaintLayer.registry.getAll().values.sortedBy { it.layer }) {
                        val temp = bufferedImage.createGraphics()

                        for ((i, layer) in frame.children.withIndex()) {
                            if (v.layer == LayerCategory.GRID && !layer.visible) continue

                            v.paint(doc, doc.selectedIndex, i, temp)
                        }

                        temp.dispose()
                    }

                    g2d.drawRenderedImage(bufferedImage, null)
                }
            }
        }
    }

    private val dropTargetAdapter = object : DropTargetAdapter() {
        override fun dragOver(dtde: DropTargetDragEvent) {
            try {
                when {
                    DnDUtil.isDnDAnImage(dtde.transferable) -> dtde.acceptDrag(DnDConstants.ACTION_COPY)
                    else -> dtde.rejectDrag()
                }
            } catch (_: IOException) {
            }
        }

        override fun drop(dtde: DropTargetDropEvent) {
            when {
                dtde.transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor) -> {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY)

                    if (DnDUtil.isDnDAnImage(dtde.transferable)) {
                        val transferable = (dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>)
                            .first()

                        if (RawkyPlugin.document == null) {
                            for ((_, v) in Opener.registry) {
                                if (transferable.extension in v.openerExtensions.flatMap { it.value }) {
                                    RawkyPlugin.document = v.open(transferable)
                                        .apply { this.path = transferable.absoluteFile }
                                    EventOpenDocument.trigger(Pair(RawkyPlugin.document!!, transferable))

                                    break
                                }
                            }
                        } else {
                            for ((_, v) in Importer.registry) {
                                if (transferable.extension in v.importerExtensions.flatMap { it.value }) {
                                    v.import(RawkyPlugin.document!!, transferable)
                                    EventImportDocument.trigger(Pair(RawkyPlugin.document!!, transferable))

                                    break
                                }
                            }
                        }

                        dtde.dropComplete(true)
                    }
                }
            }

            dtde.rejectDrop()
        }
    }

    init {
        layout = BorderLayout()

        DropTarget(panel, DnDConstants.ACTION_COPY, dropTargetAdapter, true)

        add(quickBar, BorderLayout.NORTH)
        add(panel, BorderLayout.CENTER)

        EventUpdateCell.addListener { repaint() }
    }

    fun paint(
        button: Int,
        dragged: Boolean,
        count: Int,
        cells: List<Cell<Any>> = selectedCells,
        tool: Tool<*> = Tool.current,
    ) {
        RawkyPlugin.document?.let { doc -> if (doc.selectedIndex >= doc.children.size) return }

        for (cell in cells) {
            if (cell.grid.layer.lock) continue

            tool.perform(
                cell,
                button,
                dragged,
                count,
            )

            EventUpdateCell.trigger(cell)
        }
    }
}
