package com.deflatedpickle.rawky.server.backend.request

data class RequestChangeName(
    val id: Int = -1,
    val deadName: String = "",
    val realName: String = "",
) : Request()