/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate", "unused", "LeakingThis")

package com.deflatedpickle.rawky.api

import com.deflatedpickle.marvin.registry.Registry

abstract class ControlMode : HasName {
    companion object :
        HasRegistry<String, ControlMode>,
        HasCurrent<ControlMode>,
        HasDefault<ControlMode> {
        override val registry = Registry<String, ControlMode>()
        override lateinit var current: ControlMode
        override lateinit var default: ControlMode
    }

    abstract fun apply()

    abstract fun remove()

    override fun toString() = name
}
