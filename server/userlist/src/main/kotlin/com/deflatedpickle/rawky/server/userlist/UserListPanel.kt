/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.userlist

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.event.EventStartServer
import com.deflatedpickle.rawky.server.backend.event.EventUserJoinServer
import com.deflatedpickle.rawky.server.backend.request.RequestChangeName
import com.deflatedpickle.rawky.server.backend.util.User
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.JButton
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.awt.BorderLayout
import javax.swing.DefaultListCellRenderer
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.JToolBar
import javax.swing.ListSelectionModel

object UserListPanel : PluginPanel() {
    private val hostBar =
        JToolBar("Host Settings").apply {
            // TODO: Add a way to kick users
            add(JButton("Kick"))
        }
    private val userBar =
        JToolBar("User Settings").apply {
            add(JButton("Leave") { ServerPlugin.leaveServer() })
            add(
                JButton("Rename") {
                    ServerPlugin.client.sendTCP(
                        RequestChangeName(
                            ServerPlugin.id,
                            ServerPlugin.userMap[ServerPlugin.id]!!.userName,
                            TaskDialogs.input(
                                Haruhi.window,
                                "Change Name",
                                "Input new name",
                                ServerPlugin.userMap[ServerPlugin.id]!!.userName,
                            ),
                        ),
                    )
                },
            )
        }

    private val model = DefaultListModel<User>()
    val list =
        JList(model)
            .apply { selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION }
            .apply {
                cellRenderer =
                    object : DefaultListCellRenderer() {
                        override fun getListCellRendererComponent(
                            list: JList<*>?,
                            value: Any?,
                            index: Int,
                            isSelected: Boolean,
                            cellHasFocus: Boolean,
                        ) =
                            super.getListCellRendererComponent(
                                list,
                                (value as User).let { "${it.userName} (#${it.id})" },
                                index,
                                isSelected,
                                cellHasFocus,
                            )
                    }
            }

    init {
        layout = BorderLayout()

        EventStartServer.addListener {
            add(hostBar, BorderLayout.NORTH)
            UserListPanel.repaint()
        }

        EventUserJoinServer.addListener {
            add(userBar, BorderLayout.SOUTH)
            UserListPanel.repaint()
        }

        EventChangeTheme.addListener {
            hostBar.updateUIRecursively()
            userBar.updateUIRecursively()
        }

        add(JScrollPane(list), BorderLayout.CENTER)
    }
}
