/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.api

enum class Service(val address: String) {
    AWS("http://checkip.amazonaws.com"),
    ECHO("https://ipecho.net/plain"),
}
