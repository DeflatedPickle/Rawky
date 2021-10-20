package com.deflatedpickle.rawky.server.backend.util.user

import com.deflatedpickle.marvin.Colour
import com.deflatedpickle.undulation.extensions.toColour
import java.awt.Color
import java.awt.Point

data class Host(
    override val id: Int = -1,
    override val userName: String = "",
    override val mousePosition: Point = IUser.originPoint,
    override val colour: Colour = Color.BLACK.toColour(),
) : IUser