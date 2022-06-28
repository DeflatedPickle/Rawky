package com.deflatedpickle.rawky.api

import com.deflatedpickle.marvin.registry.Registry

interface HasRegistry<K, V> {
    val registry: Registry<K, V>
}