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
import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.launcher.LauncherPlugin
import com.deflatedpickle.rawky.launcher.LauncherSettings
import com.deflatedpickle.rawky.util.ActionUtil
import com.deflatedpickle.undulation.api.MenuButtonType
import com.deflatedpickle.undulation.functions.JMenu
import com.deflatedpickle.undulation.functions.extensions.add
import com.deflatedpickle.undulation.functions.extensions.getScreenDevice
import java.awt.event.KeyEvent
import javax.swing.AbstractButton
import javax.swing.Box
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.KeyStroke
import kotlin.system.exitProcess

object MenuBar : JMenuBar() {
    private val menuRegistry = Registry<String, JMenu>()

    val fileMenu = JMenu("File", KeyEvent.VK_F)
    val viewMenu = JMenu("View", KeyEvent.VK_V)
    val toolsMenu = JMenu("Tools", KeyEvent.VK_T)
    val windowMenu = JMenu("Window", KeyEvent.VK_W)
    val help = JMenu("Help", KeyEvent.VK_H)

    init {
        (RegistryUtil.register(MenuCategory.MENU.name, menuRegistry) as Registry<String, JMenu>).apply {
            register(MenuCategory.FILE.name, fileMenu)
            register(MenuCategory.VIEW.name, viewMenu)
            register(MenuCategory.TOOLS.name, toolsMenu)
            register(MenuCategory.WINDOW.name, windowMenu)
            register(MenuCategory.HELP.name, helpMenu)
        }

        add(fileMenu)
        add(viewMenu)
        add(toolsMenu)
        add(windowMenu)
        add(Box.createGlue())
        add(helpMenu)

        EventProgramFinishSetup.addListener {
            populateFileMenu()
            populateViewMenu()
            populateWindowMenu()
        }
    }

    override fun getHelpMenu() = help

    private fun populateFileMenu() {
        fileMenu.apply {
            add(
                "New",
                MonoIcon.FOLDER_NEW,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
                "Create a new project",
            ) {
                ActionUtil.newFile()
            }

            add(
                "Open",
                MonoIcon.FOLDER_OPEN,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK),
                "Open a file in the editor",
            ) {
                LauncherPlugin.openDialog(this)
            }

            ConfigUtil.getSettings<LauncherSettings>("deflatedpickle@launcher#*")?.let {
                if (it.history.isNotEmpty()) {
                    add(LauncherPlugin.historyMenu)
                }
            }

            addSeparator()

            add(
                "Import",
                MonoIcon.FILE_NEW,
                KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK),
                "Import a file into the editor",
            ) {
                LauncherPlugin.importDialog()
            }
            add(
                "Export",
                MonoIcon.FILE_EXPORT,
                KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK),
                "Export the current project",
            ) {
                LauncherPlugin.exportDialog()
            }

            addSeparator()

            // TODO: add an item to print the file
            // TODO: add an item to send the file by email

            add(
                "Exit",
                MonoIcon.EXIT,
                KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK),
                "Close the program",
            ) {
                exitProcess(0)
            }

            addSeparator()
        }
    }

    private fun populateViewMenu() {
        viewMenu.apply {
            add(
                "Fullscreen",
                accelerator = KeyStroke.getKeyStroke("F11"),
                message = "Toggle fullscreen view",
                type = MenuButtonType.CHECK
            ) {
                if ((it.source as AbstractButton).isSelected) {
                    Window.getScreenDevice()?.fullScreenWindow = Window
                } else {
                    Window.getScreenDevice()?.fullScreenWindow = null
                }
            }
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
                                            add(v.tabText, message = "Toggle ${v.tabText}", type = MenuButtonType.CHECK) {
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

                    add("Restore Default Layout", accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F12, KeyEvent.SHIFT_DOWN_MASK)) {
                        // TODO: cache a layout when it's selected, restore to that one
                        DockingState.restoreApplicationLayout(DockingLayouts.getLayout("sprite"))
                    }
                },
            )
        }
    }
}
