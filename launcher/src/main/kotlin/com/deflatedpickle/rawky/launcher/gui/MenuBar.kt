/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.launcher.gui

import ModernDocking.Docking
import ModernDocking.DockingState
import ModernDocking.event.LayoutAdapter
import ModernDocking.internal.DockingInternal
import ModernDocking.internal.DockingListeners
import ModernDocking.layouts.ApplicationLayout
import ModernDocking.layouts.DockingLayouts
import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.event.EventSaveDocument
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.FilterCollection
import com.deflatedpickle.rawky.api.ResampleCollection
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.dialog.NewFrameDialog
import com.deflatedpickle.rawky.dialog.NewLayerDialog
import com.deflatedpickle.rawky.event.EventChangeFrame
import com.deflatedpickle.rawky.event.EventChangeLayer
import com.deflatedpickle.rawky.event.EventNewFrame
import com.deflatedpickle.rawky.event.EventNewLayer
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.event.packet.PacketChange
import com.deflatedpickle.rawky.launcher.LauncherPlugin
import com.deflatedpickle.rawky.api.ImportAs
import com.deflatedpickle.rawky.launcher.api.ScreenShotArea
import com.deflatedpickle.rawky.launcher.gui.dialog.AboutDialog
import com.deflatedpickle.rawky.launcher.gui.dialog.ApplyFilterDialog
import com.deflatedpickle.rawky.launcher.gui.dialog.ScaleImageDialog
import com.deflatedpickle.rawky.launcher.gui.dialog.ScreenshotDialog
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.util.ActionUtil
import com.deflatedpickle.rawky.util.CommonMenuItems
import com.deflatedpickle.undulation.api.MenuButtonType
import com.deflatedpickle.undulation.functions.JMenu
import com.deflatedpickle.undulation.functions.JMenuItem
import com.deflatedpickle.undulation.functions.extensions.add
import com.deflatedpickle.undulation.functions.extensions.getScreenDevice
import com.jhlabs.image.GaussianFilter
import com.jhlabs.image.ShadowFilter
import org.oxbow.swingbits.dialog.task.TaskDialog.StandardCommand
import java.awt.Color
import java.awt.Desktop
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import java.net.URI
import javax.imageio.ImageIO
import javax.swing.AbstractButton
import javax.swing.Box
import javax.swing.JFileChooser
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.KeyStroke
import javax.swing.Timer
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.reflect.full.createInstance
import kotlin.system.exitProcess

object MenuBar : JMenuBar() {
    private val menuRegistry = Registry<String, JMenu>()

    val fileMenu = JMenu("File", KeyEvent.VK_F)
    val editMenu = JMenu("Edit", KeyEvent.VK_E)
    val viewMenu = JMenu("View", KeyEvent.VK_V)
    val imageMenu = JMenu("Image", KeyEvent.VK_M)
    val frameMenu = JMenu("Frame", KeyEvent.VK_F)
    val layerMenu = JMenu("Layer", KeyEvent.VK_L)
    val toolsMenu = JMenu("Tools", KeyEvent.VK_T)
    val windowMenu = JMenu("Window", KeyEvent.VK_W)
    val help = JMenu("Help", KeyEvent.VK_H)

    val aboutItem = JMenuItem(
        "About",
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_DOWN_MASK),
        message = "Show information about Rawky",
    ) {
        val dialog = AboutDialog()
        dialog.isVisible = true
    }

    val exitItem = JMenuItem(
        "Exit",
        MonoIcon.EXIT,
        KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK),
        "Close the program",
    ) {
        exitProcess(0)
    }

    val reloadDiskItem = JMenuItem(
        "Reload from Disk",
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK),
        enabled = RawkyPlugin.document != null && RawkyPlugin.document!!.path != null,
    ) {
        LauncherPlugin.open(RawkyPlugin.document!!.path!!)
    }

    val takeScreenshotItem = JMenuItem(
        "Take Screenshot...",
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK)
    ) {
        saveScreenshot()
    }

    private val disabledUntilFile = mutableListOf<JMenuItem>()

    private val screenshotChooser = JFileChooser(File(".")).apply {
        addChoosableFileFilter(
            FileNameExtensionFilter(
                "Portable Network Graphics (\"*.png\")",
                "png",
            ),
        )
    }

    init {
        (RegistryUtil.register(MenuCategory.MENU.name, menuRegistry) as Registry<String, JMenu>).apply {
            register(MenuCategory.FILE.name, fileMenu)
            register(MenuCategory.EDIT.name, editMenu)
            register(MenuCategory.VIEW.name, viewMenu)
            register(MenuCategory.IMAGE.name, imageMenu)
            register(MenuCategory.TOOLS.name, toolsMenu)
            register(MenuCategory.WINDOW.name, windowMenu)
            register(MenuCategory.HELP.name, helpMenu)
        }

        add(fileMenu)
        add(editMenu)
        add(viewMenu)
        add(imageMenu)
        add(frameMenu)
        add(layerMenu)
        add(toolsMenu)
        add(windowMenu)
        add(Box.createGlue())
        add(helpMenu)

        EventProgramFinishSetup.addListener {
            populateFileMenu()
            populateEditMenu()
            populateViewMenu()
            populateImageMenu()
            populateFrameMenu()
            populateLayerMenu()
            populateToolsMenu()
            populateWindowMenu()
            populateHelpMenu()
        }

        EventCreateDocument.addListener {
            for (i in disabledUntilFile) {
                i.isEnabled = true
            }
        }

        EventOpenDocument.addListener {
            for (i in disabledUntilFile) {
                i.isEnabled = true
            }

            val path = (it.first as RawkyDocument).path
            if (path != null && path.exists()) {
                reloadDiskItem.isEnabled = true
            }
        }

        EventSaveDocument.addListener {
            for (i in disabledUntilFile) {
                i.isEnabled = true
            }

            val path = (it.first as RawkyDocument).path
            if (path != null && path.exists()) {
                reloadDiskItem.isEnabled = true
            }
        }
    }

    override fun getHelpMenu() = help

    private fun populateFileMenu() {
        fileMenu.apply {
            add(
                "New...",
                MonoIcon.FOLDER_NEW,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
                "Create a new project",
            ) {
                ActionUtil.newFile()
            }

            // TODO: new from screenshot

            add(
                "Open...",
                MonoIcon.FOLDER_OPEN,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK),
                "Open a file in the editor",
            ) {
                LauncherPlugin.openDialog()
            }

            add(LauncherPlugin.historyMenu)

            add(
                "Open as Frames...",
                message = "Open a series of files as independent frames",
            ) {
                LauncherPlugin.multiOpenDialog(ImportAs.FRAMES)
            }

            add(
                "Open as Layers...",
                message = "Open a series of files as layers in a given frame",
            ) {
                LauncherPlugin.multiOpenDialog(ImportAs.LAYERS)
            }

            addSeparator()

            add(reloadDiskItem)

            addSeparator()

            add(
                "Import...",
                MonoIcon.FILE_NEW,
                KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK),
                "Import a file into the editor",
            ) {
                LauncherPlugin.importDialog()
            }.also { disabledUntilFile.add(it) }

            add(
                "Export...",
                MonoIcon.FILE_EXPORT,
                KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK),
                "Export the current project",
            ) {
                LauncherPlugin.exportDialog()
            }.also { disabledUntilFile.add(it) }

            addSeparator()

            // TODO: add an item to print the file
            // TODO: add an item to send the file by email

            add(
                "Show in File Explorer",
                enabled = Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR),
            ) {
                Desktop.getDesktop().browseFileDirectory(RawkyPlugin.document?.path ?: File(System.getProperty("user.dir")))
            }

            addSeparator()

            add(
                "Close File",
                enabled = RawkyPlugin.document != null,
            ) {
                RawkyPlugin.document?.let { doc ->
                    val frame = doc.children[doc.selectedIndex]
                    val layer = frame.children[frame.selectedIndex]

                    RawkyPlugin.document = null
                    EventUpdateGrid.trigger(layer.child)

                    RawkyDocument.suggestedName = null
                    RawkyDocument.suggestedExtension = null
                }
            }.also { disabledUntilFile.add(it) }

            add(exitItem)

            addSeparator()
        }
    }

    private fun populateEditMenu() {
        editMenu.apply {
            add(CommonMenuItems.undoItem())
            add(CommonMenuItems.redoItem())

            addSeparator()

            add(
                "Clear",
                accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
                message = "Reset all cells",
                enabled = RawkyPlugin.document != null,
            ) {
                RawkyPlugin.document?.let { doc ->
                    for (frame in doc) {
                        for (layer in frame) {
                            for (cell in layer.child) {
                                cell.content = CellProvider.current.default
                            }
                        }
                    }

                    val frame = doc.children[doc.selectedIndex]
                    val layer = frame.children[frame.selectedIndex]

                    EventUpdateGrid.trigger(layer.child)
                }
            }.also { disabledUntilFile.add(it) }
        }
    }

    private fun populateViewMenu() {
        viewMenu.apply {
            add(
                JMenu("Appearance", KeyEvent.VK_A).apply {
                    add(
                        "Menu Bar",
                        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.ALT_DOWN_MASK),
                        message = "Show/hide the menu bar",
                        type = MenuButtonType.CHECK,
                    ) {
                        MenuBar.isVisible = (it.source as AbstractButton).isSelected
                    }.apply {
                        this.isSelected = MenuBar.isVisible
                    }

                    add(
                        "Toolbar",
                        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.ALT_DOWN_MASK),
                        message = "Show/hide the toolbar",
                        type = MenuButtonType.CHECK,
                    ) {
                        ToolBar.isVisible = (it.source as AbstractButton).isSelected
                    }.apply {
                        this.isSelected = ToolBar.isVisible
                    }

                    add(
                        "Status Bar",
                        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.ALT_DOWN_MASK),
                        message = "Show/hide the status bar",
                        type = MenuButtonType.CHECK,
                    ) {
                        StatusBar.isVisible = (it.source as AbstractButton).isSelected
                    }.apply {
                        this.isSelected = StatusBar.isVisible
                    }
                },
            )

            addSeparator()

            add(
                "Fullscreen",
                accelerator = KeyStroke.getKeyStroke("F11"),
                message = "Toggle fullscreen view",
                enabled = Window.getScreenDevice()?.isFullScreenSupported ?: false,
                type = MenuButtonType.CHECK,
            ) {
                if ((it.source as AbstractButton).isSelected) {
                    Window.getScreenDevice()?.fullScreenWindow = Window
                } else {
                    Window.getScreenDevice()?.fullScreenWindow = null
                }
            }

            add(
                "Always on Top",
                accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F12, KeyEvent.ALT_DOWN_MASK),
                message = "Toggle top level",
                enabled = Toolkit.getDefaultToolkit().isAlwaysOnTopSupported,
                type = MenuButtonType.CHECK,
            ) {
                Window.isAlwaysOnTop = (it.source as AbstractButton).isSelected
            }
        }
    }

    private fun populateImageMenu() {}

    private fun populateFrameMenu() {
        frameMenu.apply {
            add(
                "New...",
                message = "Create a new frame and add it to the document",
                enabled = false
            ) {
                RawkyPlugin.document?.let { doc ->
                    val frameDialog = NewFrameDialog()
                    frameDialog.isVisible = true

                    if (frameDialog.result == StandardCommand.OK) {
                        val layerDialog = NewLayerDialog(0)
                        layerDialog.isVisible = true

                        if (layerDialog.result == StandardCommand.OK) {
                            val properFrameName =
                                if (frameDialog.nameInput.text == "") null else frameDialog.nameInput.text

                            val frame = doc.addFrame(properFrameName, frameDialog.indexInput.value as Int)

                            val properLayerName =
                                if (layerDialog.nameInput.text == "") null else layerDialog.nameInput.text

                            val layer =
                                frame.addLayer(
                                    properLayerName,
                                    layerDialog.columnInput.value as Int,
                                    layerDialog.rowInput.value as Int,
                                    // layerDialog.indexInput.value as Int
                                )

                            EventNewFrame.trigger(frame)
                            EventNewLayer.trigger(layer)
                            EventChangeFrame.trigger(
                                PacketChange(
                                    System.nanoTime(),
                                    PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                                    frame,
                                    frame,
                                ),
                            )
                        }
                    }
                }
            }.also { disabledUntilFile.add(it) }
        }
    }

    private fun populateLayerMenu() {
        layerMenu.apply {
            add(
                "New...",
                message = "Create a new layer and add it to the current frame",
                enabled = false
            ) {
                RawkyPlugin.document?.let { doc ->
                    val layerDialog = NewLayerDialog()
                    layerDialog.isVisible = true

                    if (layerDialog.result == StandardCommand.OK) {
                        val layer =
                            doc.children[doc.selectedIndex].addLayer(
                                layerDialog.nameInput.text,
                                layerDialog.columnInput.value as Int,
                                layerDialog.rowInput.value as Int,
                                // layerDialog.indexInput.value as Int
                            )

                        EventNewLayer.trigger(layer)
                        EventChangeLayer.trigger(
                            PacketChange(
                                System.nanoTime(),
                                PluginUtil.slugToPlugin("deflatedpickle@layer_list")!!,
                                layer,
                                layer,
                            ),
                        )
                    }
                }
            }.also { disabledUntilFile.add(it) }

            add(
                "Scale...",
                message = "Resize the selected layer using a given algorithm",
            ) {
                scaleLayer()
            }.also { disabledUntilFile.add(it) }

            // TODO: add a rotate item

            makeFilterMenu(this) { filterLayer(it) }
        }
    }

    private fun populateToolsMenu() {
        toolsMenu.apply {
            add(takeScreenshotItem)

            addSeparator()
        }
    }

    private fun populateWindowMenu() {
        windowMenu.apply {
            add(
                JMenu("Dock").apply {
                    add(
                        JMenu("Dockables").apply {
                            DockingListeners.addLayoutListener(
                                object : LayoutAdapter() {
                                    override fun layoutDeployed(layout: ApplicationLayout) {
                                        removeAll()
                                        for (v in DockingInternal.getAllDockables()) {
                                            add(
                                                v.tabText,
                                                message = "Toggle ${v.tabText}",
                                                type = MenuButtonType.CHECK,
                                            ) {
                                                if ((it.source as AbstractButton).isSelected) {
                                                    Docking.dock(v, Window)
                                                } else {
                                                    Docking.undock(v)
                                                }
                                            }
                                                .apply { isSelected = Docking.isDocked(v) }
                                        }
                                    }
                                },
                            )
                        },
                    )

                    addSeparator()

                    add(
                        JMenu("Load").apply {
                            for (l in DockingLayouts.getLayoutNames()) {
                                add(l.capitalize()) {
                                    DockingState.restoreApplicationLayout(DockingLayouts.getLayout(l))
                                }
                            }
                        },
                    )

                    addSeparator()

                    add(
                        "Restore Default Layout",
                        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F12, KeyEvent.SHIFT_DOWN_MASK),
                    ) {
                        // TODO: cache a layout when it's selected, restore to that one
                        DockingState.restoreApplicationLayout(DockingLayouts.getLayout("sprite"))
                    }
                },
            )
        }
    }

    private fun populateHelpMenu() {
        helpMenu.apply {
            add(
                "Open wiki",
                message = "Open the Rawky wiki on GitHub",
                enabled = Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE),
            ) {
                Desktop.getDesktop().browse(URI("https://github.com/DeflatedPickle/Rawky/wiki"))
            }

            addSeparator()

            add(
                "Submit a Bug Report",
                message = "Submit a new bug report on GitHub",
                enabled = Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE),
            ) {
                Desktop.getDesktop().browse(URI("https://github.com/DeflatedPickle/Rawky/issues"))
            }

            addSeparator()

            add(aboutItem)
        }
    }

    private fun saveScreenshot() {
        val dialog = ScreenshotDialog()
        dialog.isVisible = true

        if (dialog.result == StandardCommand.OK) {
            val area = dialog.areaComboBox.selectedItem as ScreenShotArea
            var delay = dialog.delaySpinner.value * 1000
            val decoration = dialog.includeDecoration.isSelected
            val shadow = dialog.addShadow.isSelected
            val open = dialog.openCheckBox.isSelected

            // we do this as otherwise the dialog is in the screenshot
            if (delay == 0 && area == ScreenShotArea.PROGRAM && decoration) {
                delay = 200
            }

            Timer(delay) {
                var img = when (area) {
                    // TODO: add a drop shadow
                    ScreenShotArea.PROGRAM -> if (decoration) {
                        dialog.isVisible = false
                        BufferedImage(Window.width, Window.height, BufferedImage.TYPE_INT_ARGB).apply {
                            graphics.drawImage(Robot().createScreenCapture(Window.bounds), 0, 0, null)
                        }
                    } else {
                        BufferedImage(Window.contentPane.width, Window.contentPane.height, BufferedImage.TYPE_INT_ARGB).apply {
                            Window.contentPane.paint(this.graphics)
                        }
                    }
                    ScreenShotArea.SCREEN -> {
                        val size = Toolkit.getDefaultToolkit().screenSize
                        // TODO: add a graphics device selector
                        Robot().createScreenCapture(Rectangle(0, 0, size.width, size.height))
                    }
                }

                if (shadow) {
                    val radius = 12
                    val offset = 10

                    var shadow = ShadowFilter(radius.toFloat(), 0f, -offset.toFloat(), 0.8f).apply {
                        angle = 90f
                        addMargins = true
                        shadowOnly = true
                    }.filter(img, null)
                    shadow = GaussianFilter(radius.toFloat()).filter(shadow, null)

                    img = BufferedImage(img.width + radius, img.height + offset + radius, BufferedImage.TYPE_INT_ARGB).apply {
                        graphics.drawImage(shadow, -radius / 2, offset, null)
                        graphics.drawImage(img, radius / 2, 0, null)
                    }
                }

                if (screenshotChooser.showSaveDialog(Haruhi.window) == JFileChooser.APPROVE_OPTION) {
                    ImageIO.write(img, "png", screenshotChooser.selectedFile)

                    if (open) {
                        Desktop.getDesktop().open(screenshotChooser.selectedFile)
                    }
                }
            }.apply {
                isRepeats = false
                start()
            }
        }
    }

    private fun scaleLayer() {
        val dialog = ScaleImageDialog("Layer")
        dialog.isVisible = true

        if (dialog.result == StandardCommand.OK) {
            RawkyPlugin.document?.let { doc ->
                val frame = doc.children[doc.selectedIndex]
                val layer = frame.children[frame.selectedIndex]

                val layerIndex = frame.selectedIndex

                (dialog.resamplerComboBox.selectedItem as ResampleCollection.Resampler).resample(
                    dialog.columnInput.value as Int,
                    dialog.rowInput.value as Int,
                    BufferedImage(doc.columns, doc.rows, doc.colourChannel.code).apply {
                        for (row in 0 until this.height) {
                            for (column in 0 until this.width) {
                                setRGB(column, row, (layer.child[column, row].content as Color).rgb)
                            }
                        }
                    },
                ).apply {
                    val newLayer =
                        Layer(name = layer.name, child = Grid(rows = this.height, columns = this.width))
                    frame.children[layerIndex] = newLayer

                    for (row in 0 until height) {
                        for (column in 0 until width) {
                            newLayer.child[column, row].content = Color(getRGB(column, row), true)
                        }
                    }

                    doc.columns = dialog.columnInput.value as Int
                    doc.rows = dialog.rowInput.value as Int

                    EventUpdateGrid.trigger(newLayer.child)
                    EventChangeLayer.trigger(
                        PacketChange(
                            new = newLayer,
                            old = layer,
                            source = PluginUtil.slugToPlugin("deflatedpickle@launcher#*")!!,
                        ),
                    )
                }
            }
        }
    }

    fun makeFilterMenu(menu: JMenu, action: (FilterCollection.Filter) -> Unit) {
        menu.add(
            JMenu("Filter").apply {
                for (i in FilterCollection.current.filters) {
                    if (i.category != null) {
                        this.menuComponents.filterIsInstance<JMenu>().firstOrNull { it.text == i.category }
                            ?: JMenu(i.category).also {
                                add(it)
                            }
                    } else {
                        this
                    }.add(
                        i.name + if (i is FilterCollection.ArgumentFilter<*>) "..." else "",
                        message = i.comment,
                    ) {
                        action(i)
                    }.also { disabledUntilFile.add(it) }
                }
            },
        )
    }

    private fun filterLayer(i: FilterCollection.Filter) {
        RawkyPlugin.document?.let { doc ->
            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]

            val layerIndex = frame.selectedIndex

            val packet: FilterCollection.ArgumentFilter.Packet? = when (i) {
                is FilterCollection.ArgumentFilter<*> -> i.packetClass.createInstance()
                else -> null
            }

            if (packet != null) {
                val dialog = ApplyFilterDialog(packet)
                dialog.isVisible = true
                if (dialog.result != StandardCommand.OK) return@let
            }

            val tempImage = BufferedImage(doc.columns, doc.rows, doc.colourChannel.code).apply {
                for (row in 0 until this.height) {
                    for (column in 0 until this.width) {
                        setRGB(column, row, (layer.child[column, row].content as Color).rgb)
                    }
                }
            }

            when {
                packet != null && i is FilterCollection.ArgumentFilter<*> -> i.filter(
                    packet,
                    tempImage,
                )

                else -> i.filter(tempImage)
            }.apply {
                val newLayer =
                    Layer(name = layer.name, child = Grid(rows = this.height, columns = this.width))
                frame.children[layerIndex] = newLayer

                for (row in 0 until height) {
                    for (column in 0 until width) {
                        newLayer.child[column, row].content = Color(getRGB(column, row), true)
                    }
                }

                EventUpdateGrid.trigger(newLayer.child)
                EventChangeLayer.trigger(
                    PacketChange(
                        new = newLayer,
                        old = layer,
                        source = PluginUtil.slugToPlugin("deflatedpickle@launcher#*")!!,
                    ),
                )
            }
        }
    }
}
