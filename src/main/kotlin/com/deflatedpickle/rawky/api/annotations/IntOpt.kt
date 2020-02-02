/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.annotations

import kotlin.Int

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class IntOpt(val min: Int, val max: Int, val step: Int = 1)
