package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.server.backend.util.User

data class ResponseActiveUsers(
    val activeUsers: Map<Int, User> = emptyMap()
) : Response()