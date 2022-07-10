package com.deflatedpickle.rawky.server.chat

import com.deflatedpickle.rawky.server.backend.util.User
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

data class Message(
    val user: User? = null,
    val message: String = "",
    val time: String = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date()),
)