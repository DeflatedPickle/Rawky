/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.annotations

import com.deflatedpickle.rawky.api.component.Component
import kotlin.reflect.KClass

/**
 * Components annotated with this will listen to mouse events from the parent and will redraw itself when the mouse is;
 * - Released
 * - Pressed
 * - Dragged
 */
annotation class RedrawSensitive<T : Component>(val parent: KClass<T>)
