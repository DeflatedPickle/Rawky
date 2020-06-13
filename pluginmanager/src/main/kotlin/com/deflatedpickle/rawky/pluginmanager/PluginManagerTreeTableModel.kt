package com.deflatedpickle.rawky.pluginmanager

import com.deflatedpickle.rawky.api.plugin.Plugin
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import org.jdesktop.swingx.treetable.DefaultTreeTableModel

class PluginManagerTreeTableModel(val pluginList: Array<Plugin>) : DefaultTreeTableModel() {
    private val COLUMNS = arrayOf("ID", "Version")

    init {
        this.root = DefaultMutableTreeTableNode("root")
    }

    override fun getColumnCount(): Int = this.COLUMNS.size
    override fun getColumnName(column: Int): String = this.COLUMNS[column]
    override fun isCellEditable(node: Any?, column: Int): Boolean = false

    override fun isLeaf(node: Any?): Boolean = node is Plugin

    override fun getChildCount(parent: Any?): Int {
        if (parent is Plugin) {
            return parent.dependencies.size
        }
        return this.pluginList.size
    }

    override fun getChild(parent: Any?, index: Int): Any {
        if (parent is Plugin) {
            return parent.dependencies[index]
        }
        return this.pluginList[index]
    }

    override fun getIndexOfChild(parent: Any?, child: Any?): Int = 0

    override fun getValueAt(node: Any?, column: Int): Any? {
        return when (node) {
            is Plugin -> {
                when (column) {
                    0 -> node.value
                    1 -> node.version
                    else -> null
                }
            }
            else -> null
        }
    }
}