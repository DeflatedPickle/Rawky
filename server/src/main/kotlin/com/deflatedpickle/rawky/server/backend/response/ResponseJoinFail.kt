package com.deflatedpickle.rawky.server.backend.response

import com.deflatedpickle.rawky.server.backend.util.JoinFail

data class ResponseJoinFail(
    val reason: JoinFail = JoinFail.UNSPECIFIED
) :  Response()