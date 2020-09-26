package com.deflatedpickle.rawky.server.backend.request

data class RequestUserJoin(
    val userName: String = ""
) : Request()