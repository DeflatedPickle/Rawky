/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util

import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.impex.Importer
import com.deflatedpickle.rawky.api.impex.Opener
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.io.File

object DnDUtil {
    fun isDnDAnImage(transferable: Transferable): Boolean {
        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            val fileList = (transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>)

            // TODO: could accept multiple files?
            if (fileList.size > 1) return false

            val file = fileList.first()

            // TODO: could support folders?
            if (!file.isFile) return false

            // no document, open image
            if (RawkyPlugin.document == null) {
                if (file.extension in Opener.registry.values.flatMap { it.openerExtensions.values.flatten() }) {
                    return true
                }
            }
            // import image into document
            else {
                if (file.extension in Importer.registry.values.flatMap { it.importerExtensions.values.flatten() }) {
                    return true
                }
            }
        }

        return false
    }
}
