package com.deflatedpickle.rawky.api

import com.deflatedpickle.marvin.registry.Registry

data class TemplateSize(
    val name: String,
    val width: Int,
    val height: Int,
) {
    companion object {
        val registry = Registry<String, TemplateSize>()
    }

    init {
        registry[name] = this
    }

    override fun toString() = name
}
