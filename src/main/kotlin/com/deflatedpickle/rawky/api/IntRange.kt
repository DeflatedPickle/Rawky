package com.deflatedpickle.rawky.api

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class IntRange(val min: Int, val max: Int, val step: Int = 1)