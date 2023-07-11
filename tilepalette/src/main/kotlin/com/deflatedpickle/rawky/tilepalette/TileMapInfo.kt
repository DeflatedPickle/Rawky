/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tilepalette

import com.deflatedpickle.undulation.serializer.PointSerializer
import kotlinx.serialization.Serializable
import java.awt.Point

// TODO: add an optional background colour to be removed
// TODO: add regions of tiles that can be randomly selected between
// TODO: add padding to where the tiles start
// TODO: add an optional showcase image of tiles
@Serializable
class TileMapInfo(
    val size:
    @Serializable(PointSerializer::class)
    Point = Point(-1, -1),
    val link: String = "",
)
