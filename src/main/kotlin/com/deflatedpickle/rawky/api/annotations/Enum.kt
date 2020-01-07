package com.deflatedpickle.rawky.api.annotations

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class Enum(val enum: String, vararg val value: String)