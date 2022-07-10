package com.deflatedpickle.rawky.server.backend.event

import com.deflatedpickle.haruhi.api.event.AbstractEvent
import com.deflatedpickle.rawky.server.backend.api.Destination
import com.esotericsoftware.kryo.Kryo

object EventRegisterPackets : AbstractEvent<Pair<Kryo, Destination>>()