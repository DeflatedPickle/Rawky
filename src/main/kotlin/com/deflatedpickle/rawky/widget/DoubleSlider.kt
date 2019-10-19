package com.deflatedpickle.rawky.widget

import javax.swing.JSlider

class DoubleSlider(min: Double, max: Double, _value: Double, val factor: Double)
    : JSlider((min * factor).toInt(), (max * factor).toInt(), (_value * factor).toInt()) {
    var doubleMinimum: Double = min
        get() = field / this.factor
        set(value) {
            super.setMinimum(value.toInt())
            field = value * this.factor
        }

    var doubleMaximum: Double = max
        get() = field / this.factor
        set(value) {
            super.setMaximum(value.toInt())
            field = value * this.factor
        }

    var doubleValue: Double = _value
        get() = field / this.factor
        set(value) {
            super.setValue((value / this.factor).toInt())
            field = value * this.factor
        }
}