package com.deflatedpickle.rawky.server.backend.request

data class RequestRemoveUser(
    val id: Int = -1,
) : Request()