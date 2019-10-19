package com.deflatedpickle.rawky.widget

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

abstract class Slider<T : Number>(min: T, max: T, value: T) : JPanel() {
    lateinit var slider: JSlider
    open lateinit var stepSize: T

    lateinit var spinner: JSpinner

    class IntSliderComponent(min: Int, max: Int, value: Int) : Slider<Int>(min, max, value) {
        override var stepSize = 1

        init {
            slider = JSlider(min, max, value)
            spinner = JSpinner(SpinnerNumberModel(value, min, max, stepSize))

            slider.addChangeListener {
                spinner.value = slider.value
            }

            spinner.addChangeListener {
                slider.value = spinner.value as Int
            }

            this.add(slider, spinner)
        }
    }

    class DoubleSliderComponent(min: Double, max: Double, value: Double) : Slider<Double>(min, max, value) {
        override var stepSize = 0.1

        var switch = false

        init {
            slider = DoubleSlider(min, max, value, 100.0)
            spinner = JSpinner(SpinnerNumberModel(value, min, max, stepSize))

            slider.addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    spinner.value = slider.value / (slider as DoubleSlider).factor
                }
            })
            slider.addMouseMotionListener(object : MouseMotionAdapter() {
                override fun mouseDragged(e: MouseEvent) {
                    spinner.value = slider.value / (slider as DoubleSlider).factor
                }
            })

            spinner.addMouseMotionListener(object : MouseMotionAdapter() {
                override fun mouseMoved(e: MouseEvent) {
                    slider.value = ((spinner.value as Double) * 100).toInt()
                    switch = true
                }
            })
            spinner.addChangeListener {
                slider.value = ((spinner.value as Double) * 100).toInt()
            }

            this.add(slider, spinner)
        }
    }

    init {
        this.layout = GridBagLayout()
    }

    fun add(slider: JSlider, spinner: JSpinner) {
        add(slider, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        })
        add(spinner, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.EAST
        })
    }
}