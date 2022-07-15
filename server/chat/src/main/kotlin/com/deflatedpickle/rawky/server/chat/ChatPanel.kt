package com.deflatedpickle.rawky.server.chat

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.backend.event.EventJoinServer
import com.deflatedpickle.rawky.server.backend.event.EventUserJoinServer
import com.deflatedpickle.rawky.server.backend.event.EventStartServer
import com.deflatedpickle.rawky.server.backend.request.RequestChangeName
import com.deflatedpickle.rawky.server.backend.util.User
import com.deflatedpickle.rawky.server.chat.query.QueryDeleteChat
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import com.deflatedpickle.undulation.constraints.StickEastFinishLine
import com.deflatedpickle.undulation.constraints.StickWest
import com.deflatedpickle.undulation.functions.JButton
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import org.jdesktop.swingx.HorizontalLayout
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTextField
import org.jdesktop.swingx.prompt.BuddySupport.Position.RIGHT
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Desktop
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.DefaultListCellRenderer
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.JSeparator
import javax.swing.JToolBar
import javax.swing.ListSelectionModel
import javax.swing.UIManager
import javax.swing.border.LineBorder

object ChatPanel : PluginPanel() {
    val model = DefaultListModel<Message>()
    val list = JList(model).apply {
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
    }.apply {
        setCellRenderer { list, value, index, isSelected, cellHasFocus ->
            JXPanel().apply {
                layout = GridBagLayout()

                background = if (isSelected) {
                    UIManager.getColor("List.selectionBackground")
                } else {
                    UIManager.getColor("List.background")
                }

                add(JLabel("${ServerPlugin.userMap[value.id]?.userName} (${value.id})").apply {
                    foreground = ServerPlugin.userMap[value.id]?.colour
                }, StickWest)
                add(JSeparator(), FillHorizontal)
                add(JLabel(value.time), StickEastFinishLine)
                add(JLabel(value.message))
            }
        }
    }

    val sendField = JXTextField("Send...").apply {
        isEnabled = false
    }

    init {
        layout = BorderLayout()

        EventJoinServer.addListener {
            sendField.isEnabled = true
        }

        add(ChatToolbar, BorderLayout.NORTH)
        add(JScrollPane(list), BorderLayout.CENTER)
        add(sendField.apply {
            addKeyListener(object : KeyAdapter() {
                override fun keyReleased(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_ENTER) {
                        ChatPlugin.sendMessage(sendField.text)
                    }
                }
            })

            addBuddy(JXButton(MonoIcon.ARROW_RIGHT).apply {
                addActionListener {
                    ChatPlugin.sendMessage(sendField.text)
                }
            }, RIGHT)
        }, BorderLayout.SOUTH)
    }
}