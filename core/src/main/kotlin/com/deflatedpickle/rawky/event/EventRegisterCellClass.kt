/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.event

import com.deflatedpickle.haruhi.api.event.AbstractEvent
import com.deflatedpickle.rawky.collection.Cell
import kotlinx.serialization.modules.PolymorphicModuleBuilder

object EventRegisterCellClass : AbstractEvent<PolymorphicModuleBuilder<Cell<*>>>()
