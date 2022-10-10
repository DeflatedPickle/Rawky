package com.deflatedpickle.rawky.api.template

import kotlinx.serialization.Serializable

@Serializable
data class Guide(
    val name: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
)
