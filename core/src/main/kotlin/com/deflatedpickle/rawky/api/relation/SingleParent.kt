/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.relation

interface SingleParent<T> : Relationship, Parent {
    var child: T
}
