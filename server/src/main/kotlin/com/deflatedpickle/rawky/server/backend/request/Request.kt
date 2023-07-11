/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.request

import com.deflatedpickle.rawky.server.backend.api.packet.ServerPacket
import com.deflatedpickle.rawky.server.backend.response.Response

/** Requests are sent to the server to get a [Response] */
abstract class Request : ServerPacket
