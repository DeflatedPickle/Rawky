package com.deflatedpickle.rawky.function

import org.apache.logging.log4j.Logger

/**
 * Runs a block, catching all exceptions thrown and logging them
 *
 * Useful for running blocks that can throw errors and not having them crash the program
 *
 * SHOULD NOT BE USED WHEN SETTING IMPORTANT INFORMATION
 */
fun umbrella(logger: Logger, action: () -> Unit) {
    try {
        action()
    } catch (error: Throwable) {
        logger.warn("Failed due to ${error.message}")
    }
}

/**
 * Runs a block, catching all exceptions thrown and logging them, then returning either null or the action
 *
 * Useful for running blocks that can throw errors and not having them crash the program
 *
 * SHOULD NOT BE USED WHEN SETTING IMPORTANT INFORMATION
 */
fun <T> umbrella(logger: Logger, action: () -> T?): T? {
    try {
        return action()
    } catch (error: Throwable) {
        logger.warn("Failed due to ${error.message}")
    }

    return null
}