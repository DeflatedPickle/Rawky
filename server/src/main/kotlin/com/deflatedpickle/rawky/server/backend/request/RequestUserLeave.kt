package com.deflatedpickle.rawky.server.backend.request

data class RequestUserLeave(
    val id: Int = -1,
    val userName: String = "",
) : Request()