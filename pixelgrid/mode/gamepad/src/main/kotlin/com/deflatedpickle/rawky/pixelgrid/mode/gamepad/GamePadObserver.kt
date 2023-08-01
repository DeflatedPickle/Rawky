/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.mode.gamepad

import net.java.games.input.Component
import net.java.games.input.Component.Identifier.Axis
import net.java.games.input.Component.Identifier.Button
import net.java.games.input.Controller
import net.java.games.input.ControllerEnvironment
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

object GamePadObserver {
    private var controller = getGamepad()

    private var run = false

    fun watch() {
        run = true
        Thread {
            while (run) {
                if (controller == null || !controller!!.poll()) {
                    controller = getGamepad()
                }

                handleButtons()
            }
        }.start()
    }

    fun blind() {
        run = false
    }

    private val listeners = mutableListOf<GamePadListener>()

    fun addListener(listener: GamePadListener) {
        listeners.add(listener)
    }

    private fun getGamepad(): Controller? =
        ControllerEnvironment
            .getDefaultEnvironment()
            .controllers
            .firstOrNull { it.type == Controller.Type.GAMEPAD }

    private var previousButtons = mutableListOf<Component>()
    private var repeatTimer = Timer()
    private var lastTask: TimerTask? = null

    fun rumble(intensity: Float, duration: Long) {
        controller?.let { controller ->
            for (i in controller.rumblers) {
                i.rumble(intensity)
            }

            Timer().schedule(duration) {
                for (i in controller.rumblers) {
                    i.rumble(0f)
                }
            }
        }
    }

    private fun handleButtons() {
        controller?.let { controller ->
            val pressedButtons = getButtons(controller)
            if (pressedButtons != previousButtons) {
                lastTask?.cancel()

                for (b in pressedButtons) {
                    for (l in listeners) {
                        l.update(GamePadEvent(b.identifier, b.pollData))
                    }
                }
                previousButtons = pressedButtons

                lastTask = repeatTimer.schedule(200) {
                    previousButtons.clear()
                }
            }
        }
    }

    private fun getButtons(controller: Controller): MutableList<Component> {
        val buttons = mutableListOf<Component>()

        for (c in controller.components) {
            when (c.identifier) {
                is Button ->
                    if (c.pollData > c.deadZone) {
                        buttons.add(c)
                        continue
                    }
                is Axis -> {
                    buttons.add(c)
                    continue
                }
            }
        }

        return buttons
    }
}
