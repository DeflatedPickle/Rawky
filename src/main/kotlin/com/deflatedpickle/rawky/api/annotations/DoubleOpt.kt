/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.annotations

import kotlin.Double

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class DoubleOpt(val min: Double, val max: Double)
