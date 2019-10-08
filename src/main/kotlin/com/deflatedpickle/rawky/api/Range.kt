package com.deflatedpickle.rawky.api

@Repeatable
@Target(AnnotationTarget.FIELD)
annotation class Range(val min: Int, val max: Int)