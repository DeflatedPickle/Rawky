/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.annotations

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class IntRangeOpt(val min: Int, val max: Int)
