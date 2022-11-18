/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.server.chat

import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.server.chat.query.QueryDeleteChat
import org.jdesktop.swingx.JXButton
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.JToolBar

object ChatToolbar : JToolBar("Chat") {
    val copyButton = JXButton(MonoIcon.COPY).apply {
        addActionListener {
            Toolkit
                .getDefaultToolkit()
                .systemClipboard
                .setContents(
                    StringSelection(ChatPanel.list.selectedValuesList.joinToString("\n") { it.message }),
                    null
                )
        }
    }

    val deleteButton = JXButton(MonoIcon.DELETE).apply {
        addActionListener {
            // TODO: Replace with role/permission check
            if (ChatPanel.list.selectedValuesList.all { it.id == ServerPlugin.id } || ServerPlugin.server != null) {
                ServerPlugin.client.sendTCP(
                    QueryDeleteChat(
                        ServerPlugin.id,
                        ChatPanel.list.selectedValuesList
                    )
                )
            }
        }
    }

    init {
        add(copyButton)
        add(deleteButton)

        ChatToolbar.components.forEach {
            it.isEnabled = false
        }

        ChatPanel.list.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                copyButton.isEnabled = true
                deleteButton.isEnabled = ChatPanel.list.selectedValuesList.all { it.id == ServerPlugin.id } || ServerPlugin.server != null
            }
        }
    }
}
