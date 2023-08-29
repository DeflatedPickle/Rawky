/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.relation

interface SingleChild<T> : Relationship, Child {
    var parent: T
}
