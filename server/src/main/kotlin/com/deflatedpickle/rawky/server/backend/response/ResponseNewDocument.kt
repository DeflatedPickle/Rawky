package com.deflatedpickle.rawky.server.backend.response

data class ResponseNewDocument(
    val rows: Int = 0,
    val columns: Int = 0,
    val frames: Int = 0,
    val layers: Int = 0,
) : Response()