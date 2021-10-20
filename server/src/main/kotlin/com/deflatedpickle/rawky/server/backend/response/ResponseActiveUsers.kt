package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.server.backend.util.user.IUser

data class ResponseActiveUsers(
    val activeUsers: Map<Int, IUser> = emptyMap()
) : Response()