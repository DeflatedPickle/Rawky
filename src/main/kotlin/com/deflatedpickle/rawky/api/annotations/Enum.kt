/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.annotations

import org.apache.commons.lang3.StringUtils

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class Enum(val enum: String, val setter: String = StringUtils.EMPTY, vararg val value: String)
