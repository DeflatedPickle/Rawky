package com.deflatedpickle.rawky.widget

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel
import com.jidesoft.swing.RangeSlider as JRangeSlider

abstract class RangeSlider<T : Number> : JPanel() {
    lateinit var slider: JRangeSlider
    open lateinit var stepSize: T

    lateinit var pastSpinner: JSpinner
    lateinit var postSpinner: JSpinner

    class IntRangeSliderComponent(min: Int, max: Int, lowValue: Int, highValue: Int) : RangeSlider<Int>() {
        override var stepSize = 1

        init {
            slider = JRangeSlider(min, max).apply {
                setLowValue(lowValue)
                setHighValue(highValue)
            }
            pastSpinner = JSpinner(SpinnerNumberModel(lowValue, min, max, stepSize))
            postSpinner = JSpinner(SpinnerNumberModel(highValue, min, max, stepSize))

            slider.addChangeListener {
                pastSpinner.value = slider.lowValue
                postSpinner.value = slider.highValue
            }

            pastSpinner.addChangeListener {
                slider.lowValue = pastSpinner.value as Int
            }
            postSpinner.addChangeListener {
                slider.highValue = postSpinner.value as Int
            }

            this.add(pastSpinner, slider, postSpinner)
        }
    }

    init {
        this.layout = GridBagLayout()
    }

    fun add(pastSpinner: JSpinner, slider: JRangeSlider, postSpinner: JSpinner) {
        add(pastSpinner, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.WEST
        })
        add(slider, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        })
        add(postSpinner, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.EAST
        })
    }
}