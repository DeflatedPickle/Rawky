package com.deflatedpickle.rawky.event

import com.pploder.events.SimpleEvent
import org.apache.logging.log4j.LogManager
import java.util.function.Consumer

abstract class AbstractEvent<T : Any> : SimpleEvent<T>() {
    protected val logger = LogManager.getLogger(this::class.simpleName)

    override fun trigger(t: T) {
        this.logger.info("This event was triggered with ${t::class}")
        super.trigger(t)
    }

    override fun addListener(listener: Consumer<T>?) {
        this.logger.debug("A listener was attached to this event")
        super.addListener(listener)
    }
}