package com.deflatedpickle.rawky.api.relation

interface MultiParent<T> : Relationship, Parent {
    val children: MutableList<T>
}