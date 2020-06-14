package com.deflatedpickle.rawky.util

import kotlin.properties.Delegates

object GeneralUtil {
    var isInDev by Delegates.notNull<Boolean>()
}