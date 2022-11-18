/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api

import com.deflatedpickle.marvin.registry.Registry

interface HasRegistry<K, V> {
    val registry: Registry<K, V>
}
