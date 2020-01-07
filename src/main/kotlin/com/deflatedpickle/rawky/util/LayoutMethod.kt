package com.deflatedpickle.rawky.util

import com.deflatedpickle.rawky.component.PixelGrid

enum class LayoutMethod(
        val rowOffsetOdd: Int,
        val rowOffsetEven: Int,
        val columnOffsetOdd: Int,
        val columnOffsetEven: Int
) {
    GRID(0, 0, 0, 0),
    HALF_OFFSET_GRID(0, PixelGrid.Settings.pixelSize / 2, 0, 0)
}