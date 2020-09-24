package com.deflatedpickle.rawky.server.backend.request

data class RequestUserJoin(
    val serverPassword: String = "",
    val userName: String = ""
) : Request()