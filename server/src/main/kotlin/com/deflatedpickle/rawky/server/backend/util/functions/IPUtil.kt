package com.deflatedpickle.rawky.server.backend.util.functions

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.ByteBuffer

fun getLocalIP(): String {
    val url = URL("http://checkip.amazonaws.com")
    val bufferedReader = BufferedReader(
        InputStreamReader(
            url.openStream()
        )
    )

    return bufferedReader.readLine()
}

fun ipToByteArray(ip: String): ByteArray =
    ip.split(".").foldIndexed(ByteArray(4)) { i, acc, s ->
        acc[i] = s.toInt().toByte()
        acc
    }

fun ipFromByteArray(byteArray: ByteArray): String = byteArray.joinToString(
    separator = ".",
    transform = {
        val int = it.toInt()
        if (int < 0) {
            int + 256
        } else {
            int
        }.toString()
    }
)

// https://stackoverflow.com/a/7619315
fun portToByteArray(port: Int): ByteArray = ByteBuffer.allocate(4).putInt(port).array()
fun portFromByteArray(byteArray: ByteArray): Int = ByteBuffer.wrap(byteArray).int