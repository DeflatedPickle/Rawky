@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.server.leaderboard

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.server.backend.util.User
import org.apache.commons.lang3.time.DurationFormatUtils
import org.jdesktop.swingx.JXTable
import java.awt.BorderLayout
import java.awt.Color
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.swing.JLabel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

object LeaderboardPanel : PluginPanel() {
    val model = object : DefaultTableModel(
        LeaderboardPlugin.stats,
        Vector(listOf("User", "Pixels", "Colours", "Playtime"))
    ) {
        override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0 -> User::class.java
                1 -> Int::class.java
                2 -> List::class.java
                3 -> Instant::class.java
                else -> Any::class.java
            }
    }

    private val table = JXTable(model).apply {
        setDefaultRenderer(User::class.java) { _: JTable, value: Any?, _: Boolean, _: Boolean, _: Int, _: Int ->
            if (value == null) {
                JLabel(MonoIcon.ERROR)
            } else {
                with(value as User) {
                    JLabel("${this.userName} (#${this.id})").apply {
                        foreground = this@with.colour
                    }
                }
            }
        }

        setDefaultRenderer(Int::class.java) { _: JTable, value: Any?, _: Boolean, _: Boolean, _: Int, _: Int ->
            if (value == null) {
                JLabel(MonoIcon.ERROR)
            } else {
                JLabel("${value as Number}")
            }
        }

        setDefaultRenderer(List::class.java) { _: JTable, value: Any?, _: Boolean, _: Boolean, _: Int, _: Int ->
            if (value == null) {
                JLabel(MonoIcon.ERROR)
            } else {
                JLabel("${(value as List<Color>).size}")
            }
        }

        setDefaultRenderer(Instant::class.java) { _: JTable, value: Any?, _: Boolean, _: Boolean, _: Int, _: Int ->
            if (value == null) {
                JLabel(MonoIcon.ERROR)
            } else {
                JLabel(
                    DurationFormatUtils.formatDuration(
                        Duration.between(
                            value as Instant,
                            Instant.now()
                        ).toMillis(),
                        "HH:mm:ss")
                )
            }
        }
    }

    init {
        layout = BorderLayout()

        add(JScrollPane(table), BorderLayout.CENTER)
    }
}