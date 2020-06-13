package com.deflatedpickle.rawky.extension

import javax.swing.table.DefaultTableModel

fun DefaultTableModel.removeAll() {
    for (i in 0 until this.rowCount) {
        this.removeRow(0)
    }
}