package com.deflatedpickle.rawky.server.backend.response

data class ResponseUserLeave(
    override val id: Int = -1,
    val userName: String = ""
) : Response()