/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.frontend.widget

import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.server.ServerSettings
import com.deflatedpickle.rawky.server.backend.api.Encoder
import javax.swing.JComboBox
import javax.swing.SwingUtilities

class EncoderComboBox : JComboBox<Encoder>() {
    init {
        SwingUtilities.invokeLater {
            initItems()
            checkDefault()
        }
    }

    private fun initItems() {
        for ((_, v) in Encoder.registry) {
            this.addItem(v)
        }
    }

    private fun checkDefault() {
        ConfigUtil.getSettings<ServerSettings>("deflatedpickle@server#*")?.let {
            selectedItem = if (it.defaultConnectionEncoding.isNotEmpty()) {
                Encoder.registry[it.defaultConnectionEncoding]
            } else {
                Encoder.registry.values.first()
            }
        }
    }
}
