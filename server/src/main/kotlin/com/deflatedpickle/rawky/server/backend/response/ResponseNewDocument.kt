/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.ColourChannel
import com.deflatedpickle.rawky.server.ServerPlugin
import com.deflatedpickle.rawky.util.ActionUtil
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection

data class ResponseNewDocument(
    val rows: Int = 0,
    val columns: Int = 0,
    val colourChannel: ColourChannel = ColourChannel.ARGB,
    val frames: Int = 0,
    val layers: Int = 0,
) : Response() {
    override fun runClient(connection: Connection, client: Client) {
        if (ServerPlugin.server == null) {
            ServerPlugin.logger.debug("Creating a new document from server info")
            RawkyPlugin.document =
                ActionUtil.newDocument(
                    rows,
                    columns,
                    colourChannel,
                    frames,
                    layers,
                )
            EventCreateDocument.trigger(RawkyPlugin.document!!)
        }
    }
}
