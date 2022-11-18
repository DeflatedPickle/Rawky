/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.relation

// A matrix can belong to a parent as well as have children
interface Matrix<C> : MultiParent<C> {
    val rows: Int
    val columns: Int
}
