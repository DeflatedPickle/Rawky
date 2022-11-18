/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.relation

interface MultiParent<T> : Relationship, Parent {
    val children: MutableList<T>
}
