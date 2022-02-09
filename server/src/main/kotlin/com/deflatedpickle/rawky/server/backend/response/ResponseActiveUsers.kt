package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.server.backend.util.User
import com.deflatedpickle.rawky.server.backend.util.UserUpdate

data class ResponseActiveUsers(
    val activeUsers: Map<Int, User> = emptyMap(),
    val reason: UserUpdate = UserUpdate.OTHER,
) : Response()