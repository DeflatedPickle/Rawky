/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.relation

interface MultiChild<T> : Relationship, Child {
    val parents: MutableList<T>
}
