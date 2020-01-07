/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.annotations

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class IntRange(val min: Int, val max: Int, val step: Int = 1)
