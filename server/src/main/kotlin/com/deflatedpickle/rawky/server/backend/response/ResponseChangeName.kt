package com.deflatedpickle.rawky.server.backend.response

data class ResponseChangeName(
    override val id: Int = -1,
    val deadName: String = "",
    val realName: String = "",
) : Response()