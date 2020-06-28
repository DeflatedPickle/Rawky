package com.deflatedpickle.rawky.event

import com.pploder.events.SimpleEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Consumer

abstract class AbstractEvent<T : Any> : SimpleEvent<T>() {
    @Suppress("MemberVisibilityCanBePrivate")
    protected val logger: Logger = LogManager.getLogger(this::class.simpleName)

    override fun trigger(t: T) {
        this.logger.debug("This event was triggered with ${t::class}")
        super.trigger(t)
    }

    override fun addListener(listener: Consumer<T>) {
        this.logger.debug("A listener was attached to this event")
        super.addListener(listener)
    }
}