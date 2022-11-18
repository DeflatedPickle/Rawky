/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.serializer

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.serializers.FieldSerializer
import java.awt.Color

class ColorSerializer(kryo: Kryo) : FieldSerializer<Color>(kryo, Color::class.java) {
    override fun write(kryo: Kryo, output: Output, `object`: Color) {
        output.writeString(
            String.format(
                "#%08x",
                (`object`.alpha and 0xFF shl 24) or (`object`.red and 0xFF shl 16) or
                    (`object`.green and 0xFF shl 8) or (`object`.blue and 0xFF shl 0)
            )
        )
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out Color>): Color {
        val intval = java.lang.Long.decode(input.readString())
        val i = intval.toInt()
        return Color(i shr 16 and 0xFF, i shr 8 and 0xFF, i and 0xFF, i shr 24 and 0xFF)
    }
}
