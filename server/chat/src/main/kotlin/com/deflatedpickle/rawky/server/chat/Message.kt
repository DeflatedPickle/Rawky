/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.chat

import java.text.SimpleDateFormat
import java.util.Date

data class Message(
    val id: Int = -1,
    val message: String = "",
    val time: String = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date()),
)
