/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.server.userlist

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.event.EventCloseServer
import com.deflatedpickle.rawky.server.backend.event.EventDisconnect
import com.deflatedpickle.rawky.server.backend.event.EventUserJoinServer
import com.deflatedpickle.rawky.server.backend.event.EventUserLeaveServer
import com.deflatedpickle.rawky.server.backend.event.EventUserRename
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import javax.swing.DefaultListModel

@Plugin(
    value = "userlist",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A widget to show a list of users
    """,
    type = PluginType.COMPONENT,
    component = UserListPanel::class,
    dependencies =
    [
        "deflatedpickle@core#*",
        "deflatedpickle@server#*",
    ],
)
object UserListPlugin {
    init {
        EventUserJoinServer.addListener {
            // The first time you join, you'll need all the other users
            if (UserListPanel.list.model.size == 0) {
                for ((_, v) in ServerPlugin.userMap) {
                    (UserListPanel.list.model as DefaultListModel).addElement(v)
                }
            } else {
                (UserListPanel.list.model as DefaultListModel).addElement(it)
            }
        }

        EventUserLeaveServer.addListener {
            (UserListPanel.list.model as DefaultListModel).removeElement(it)
            UserListPanel.list.repaint()
        }

        EventDisconnect.addListener {
            (UserListPanel.list.model as DefaultListModel).removeAllElements()
            UserListPanel.list.repaint()
        }

        EventUserRename.addListener {
            var index: Int = -1
            for (i in 0..UserListPanel.list.model.size) {
                val user = UserListPanel.list.model.getElementAt(i)

                if (it.id == user.id) {
                    index = i
                    break
                }
            }

            if (index != -1) {
                (UserListPanel.list.model as DefaultListModel).setElementAt(it, index)
            }
        }

        EventCloseServer.addListener {
            (UserListPanel.list.model as DefaultListModel).removeAllElements()
        }

        EventChangeTheme.addListener { UserListPanel.updateUIRecursively() }
    }
}
