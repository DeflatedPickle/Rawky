package com.deflatedpickle.rawky.api

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class DoubleRange(val min: Double, val max: Double)