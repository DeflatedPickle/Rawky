package com.deflatedpickle.rawky.api.relation

interface MultiParent<T> : Relationship, Parent {
    val children: Array<T>
}