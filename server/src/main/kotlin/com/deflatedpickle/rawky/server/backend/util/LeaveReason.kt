package com.deflatedpickle.rawky.server.backend.util

enum class LeaveReason {
    /**
     * The user left themselves
     */
    LEFT,

    /**
     * The user was kicked by the host
     */
    KICK,

    /**
     * The user spent too long doing nothing
     */
    TIMEOUT,

    /**
     * The user was voted out
     */
    SUS,
}