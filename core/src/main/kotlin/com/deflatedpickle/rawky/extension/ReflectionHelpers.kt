package com.deflatedpickle.rawky.extension

inline fun <reified T : Any> Any.get(string: String): T {
    val getterPrefix = when (T::class) {
        // Boolean::class -> "is"
        else -> "get"
    }

    val getterName = "${getterPrefix}${string.capitalize()}"

    val method = javaClass.getDeclaredMethod(getterName)
    method.isAccessible = true

    return method.invoke(this) as T
}

inline fun <reified T : Any> Any.set(string: String, value: T) {
    val setterPrefix = when (T::class) {
        // Boolean::class -> "has"
        else -> "set"
    }

    val setterName = "${setterPrefix}${string.capitalize()}"

    val method = javaClass.getDeclaredMethod(setterName, Boolean::class.java)
    method.isAccessible = true
    method.invoke(this, value) as? T
}