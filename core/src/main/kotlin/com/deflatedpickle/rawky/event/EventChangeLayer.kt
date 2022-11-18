/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.event

import com.deflatedpickle.haruhi.api.event.AbstractEvent
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.event.packet.PacketChange

object EventChangeLayer : AbstractEvent<PacketChange<Layer>>()
