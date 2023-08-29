/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.relation

interface Matrix<C> : MultiParent<C> {
    val rows: Int
    val columns: Int
}
