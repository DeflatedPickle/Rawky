package com.deflatedpickle.rawky.api

@Target(AnnotationTarget.FIELD)
annotation class Range(val min: Int, val max: Int)